package com.logistic.control.service;

import com.logistic.control.dto.request.DetalleDevolucionRequest;
import com.logistic.control.dto.request.DevolucionRequest;
import com.logistic.control.dto.response.DetalleDevolucionResponse;
import com.logistic.control.dto.response.DevolucionResponse;
import com.logistic.control.entity.*;
import com.logistic.control.enums.*;
import com.logistic.control.exception.BusinessException;
import com.logistic.control.exception.InvalidStateException;
import com.logistic.control.exception.ResourceNotFoundException;
import com.logistic.control.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servicio para gestión de devoluciones de ventas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DevolucionService {

    private final DevolucionVentaRepository devolucionRepository;
    private final DetalleDevolucionRepository detalleDevolucionRepository;
    private final ClienteRepository clienteRepository;
    private final FacturaRepository facturaRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final InventarioRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;

    private static final AtomicInteger devolucionCounter = new AtomicInteger(1);

    /**
     * Listar todas las devoluciones con paginación.
     */
    public Page<DevolucionResponse> listarDevoluciones(Pageable pageable) {
        log.debug("Listando devoluciones - página: {}", pageable.getPageNumber());
        return devolucionRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtener devolución por ID.
     */
    public DevolucionResponse obtenerDevolucion(Long id) {
        log.debug("Obteniendo devolución con ID: {}", id);
        DevolucionVenta devolucion = devolucionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución", "id", id));
        return toResponse(devolucion);
    }

    /**
     * Listar devoluciones pendientes de aprobación.
     */
    public List<DevolucionResponse> listarDevolucionesPendientes() {
        log.debug("Listando devoluciones pendientes");
        return devolucionRepository.findDevolucionesPendientes().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Buscar devoluciones por cliente.
     */
    public Page<DevolucionResponse> buscarPorCliente(Long clienteId, Pageable pageable) {
        log.debug("Buscando devoluciones del cliente: {}", clienteId);
        return devolucionRepository.findByClienteId(clienteId, pageable)
                .map(this::toResponse);
    }

    /**
     * Crear una nueva devolución.
     */
    @Transactional
    public DevolucionResponse crearDevolucion(DevolucionRequest request) {
        log.info("Creando devolución de tipo: {}", request.getTipo());

        // 1. Validar cliente
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClienteId()));

        // 2. Validar factura o pedido según tipo
        Factura factura = null;
        Pedido pedido = null;

        if (request.getTipo() == TipoDevolucion.PRODUCTO_FISICO ||
            request.getTipo() == TipoDevolucion.CORRECCION_FACTURA) {

            if (request.getFacturaId() == null) {
                throw new BusinessException(
                    String.format("Devolución de tipo %s requiere una factura", request.getTipo())
                );
            }

            factura = facturaRepository.findById(request.getFacturaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", request.getFacturaId()));

            if (factura.getEstado() == EstadoFactura.ANULADA) {
                throw new InvalidStateException("No se puede crear devolución de una factura anulada");
            }

            pedido = factura.getPedido();
        }

        if (request.getTipo() == TipoDevolucion.AJUSTE_PEDIDO) {
            if (request.getPedidoId() == null) {
                throw new BusinessException("Devolución de tipo AJUSTE_PEDIDO requiere un pedido");
            }

            pedido = pedidoRepository.findById(request.getPedidoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", request.getPedidoId()));

            if (pedido.getEstado() == EstadoPedido.CANCELADO) {
                throw new InvalidStateException("No se puede crear devolución de un pedido cancelado");
            }
        }

        // 3. Crear devolución
        DevolucionVenta devolucion = DevolucionVenta.builder()
                .numeroDevolucion(generarNumeroDevolucion())
                .tipo(request.getTipo())
                .estado(EstadoDevolucion.SOLICITADA)
                .factura(factura)
                .pedido(pedido)
                .cliente(cliente)
                .generarNotaCredito(request.getGenerarNotaCredito() != null ? request.getGenerarNotaCredito() : false)
                .fechaSolicitud(LocalDateTime.now())
                .motivo(request.getMotivo())
                .observaciones(request.getObservaciones())
                .detalles(new ArrayList<>())
                .build();

        // 4. Procesar detalles
        for (DetalleDevolucionRequest itemRequest : request.getDetalles()) {
            Producto producto = productoRepository.findById(itemRequest.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", itemRequest.getProductoId()));

            DetalleFactura detalleFactura = null;
            DetallePedido detallePedido = null;
            BigDecimal precioUnitario = BigDecimal.ZERO;
            Integer porcentajeIva = 10;

            // Obtener referencia al detalle original según tipo
            if (request.getTipo() == TipoDevolucion.PRODUCTO_FISICO ||
                request.getTipo() == TipoDevolucion.CORRECCION_FACTURA) {

                if (itemRequest.getDetalleFacturaId() != null) {
                    detalleFactura = detalleFacturaRepository.findById(itemRequest.getDetalleFacturaId())
                            .orElseThrow(() -> new ResourceNotFoundException("DetalleFactura", "id", itemRequest.getDetalleFacturaId()));

                    precioUnitario = BigDecimal.valueOf(detalleFactura.getPrecioUnitario());
                    porcentajeIva = detalleFactura.getPorcentajeIva();
                    detallePedido = detalleFactura.getDetallePedido();
                }
            }

            if (request.getTipo() == TipoDevolucion.AJUSTE_PEDIDO) {
                if (itemRequest.getDetallePedidoId() != null) {
                    detallePedido = detallePedidoRepository.findById(itemRequest.getDetallePedidoId())
                            .orElseThrow(() -> new ResourceNotFoundException("DetallePedido", "id", itemRequest.getDetallePedidoId()));

                    precioUnitario = detallePedido.getPrecioUnitario();
                }
            }

            // Override de precio si viene en el request
            if (itemRequest.getPrecioUnitario() != null) {
                precioUnitario = BigDecimal.valueOf(itemRequest.getPrecioUnitario());
            }

            if (itemRequest.getPorcentajeIva() != null) {
                porcentajeIva = itemRequest.getPorcentajeIva();
            }

            // Crear detalle de devolución
            DetalleDevolucion detalle = DetalleDevolucion.builder()
                    .devolucion(devolucion)
                    .producto(producto)
                    .detalleFactura(detalleFactura)
                    .detallePedido(detallePedido)
                    .cantidad(itemRequest.getCantidad())
                    .precioUnitario(precioUnitario)
                    .descuento(BigDecimal.ZERO)
                    .porcentajeIva(porcentajeIva)
                    .estadoProducto(itemRequest.getEstadoProducto())
                    .observaciones(itemRequest.getObservaciones())
                    .build();

            devolucion.getDetalles().add(detalle);
        }

        // 5. Calcular totales
        devolucion.calcularTotales();

        // 6. Validar devolución
        devolucion.validar();

        // 7. Guardar
        DevolucionVenta saved = devolucionRepository.save(devolucion);

        log.info("Devolución creada exitosamente con ID: {} - Tipo: {} - Total items: {}",
                saved.getId(), saved.getTipo(), saved.getDetalles().size());

        return toResponse(saved);
    }

    /**
     * Aprobar una devolución y procesarla automáticamente.
     */
    @Transactional
    public DevolucionResponse aprobarDevolucion(Long devolucionId, Long usuarioId) {
        log.info("Aprobando devolución ID: {} por usuario: {}", devolucionId, usuarioId);

        DevolucionVenta devolucion = devolucionRepository.findById(devolucionId)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución", "id", devolucionId));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        // 1. Aprobar
        devolucion.aprobar(usuario);

        // 2. Iniciar proceso
        devolucion.iniciarProceso();

        // 3. Procesar según tipo
        switch (devolucion.getTipo()) {
            case PRODUCTO_FISICO:
                procesarDevolucionProductoFisico(devolucion);
                break;
            case CORRECCION_FACTURA:
                procesarCorreccionFactura(devolucion);
                break;
            case AJUSTE_PEDIDO:
                procesarAjustePedido(devolucion);
                break;
        }

        // 4. Generar nota de crédito si corresponde
        if (devolucion.getGenerarNotaCredito()) {
            generarNotaCredito(devolucion);
        }

        // 5. Completar
        devolucion.completar();

        DevolucionVenta saved = devolucionRepository.save(devolucion);

        log.info("Devolución {} aprobada y procesada exitosamente", devolucionId);

        return toResponse(saved);
    }

    /**
     * Rechazar una devolución.
     */
    @Transactional
    public DevolucionResponse rechazarDevolucion(Long devolucionId, Long usuarioId, String motivo) {
        log.info("Rechazando devolución ID: {} por usuario: {}", devolucionId, usuarioId);

        DevolucionVenta devolucion = devolucionRepository.findById(devolucionId)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución", "id", devolucionId));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        devolucion.rechazar(usuario, motivo);

        DevolucionVenta saved = devolucionRepository.save(devolucion);

        log.info("Devolución {} rechazada", devolucionId);

        return toResponse(saved);
    }

    /**
     * Procesar devolución de producto físico.
     * Registra entrada en inventario y revierte cantidad facturada.
     */
    @Transactional
    protected void procesarDevolucionProductoFisico(DevolucionVenta devolucion) {
        log.info("Procesando devolución física ID: {}", devolucion.getId());

        for (DetalleDevolucion detalle : devolucion.getDetalles()) {
            // 1. Registrar entrada en inventario
            // Nota: La entidad Inventario representa stock físico, no movimientos
            // Para una devolución, se crea una nueva entrada de inventario
            Inventario entrada = Inventario.builder()
                    .cliente(devolucion.getCliente())
                    .producto(detalle.getProducto())
                    .cantidad(detalle.getCantidad())
                    .cantidadReservada(0)
                    .estado(detalle.getEstadoProducto() != null && detalle.getEstadoProducto().equals("DANIADO")
                           ? EstadoInventario.DANIADO
                           : EstadoInventario.EN_DEPOSITO)
                    .fechaEntrada(LocalDateTime.now())
                    .pedido(devolucion.getPedido())
                    .observaciones("Devolución " + devolucion.getNumeroDevolucion() +
                                  (detalle.getEstadoProducto() != null ? " - Estado: " + detalle.getEstadoProducto() : ""))
                    .build();

            entrada = inventarioRepository.save(entrada);
            detalle.setInventarioEntrada(entrada);

            log.debug("Entrada de inventario creada: {} unidades de producto {}",
                     detalle.getCantidad(), detalle.getProducto().getCodigo());

            // 2. Revertir cantidad facturada si hay detalle de pedido
            if (detalle.getDetallePedido() != null) {
                DetallePedido detallePedido = detalle.getDetallePedido();
                detallePedido.revertirFacturacion(detalle.getCantidad());
                detallePedidoRepository.save(detallePedido);

                log.debug("Revertida facturación: {} unidades del detalle pedido {}",
                         detalle.getCantidad(), detallePedido.getId());
            }
        }
    }

    /**
     * Procesar ajuste de pedido (reducir cantidades).
     */
    @Transactional
    protected void procesarAjustePedido(DevolucionVenta devolucion) {
        log.info("Procesando ajuste de pedido ID: {}", devolucion.getId());

        for (DetalleDevolucion detalle : devolucion.getDetalles()) {
            if (detalle.getDetallePedido() == null) {
                throw new BusinessException("Ajuste de pedido requiere referencia a DetallePedido");
            }

            DetallePedido detallePedido = detalle.getDetallePedido();

            // Validar que no se devuelva más de lo pendiente
            Integer cantidadPendiente = detallePedido.getCantidadPendienteFacturar();
            if (detalle.getCantidad() > cantidadPendiente) {
                throw new BusinessException(
                    String.format("No se puede devolver %d unidades. Solo hay %d pendientes de facturar",
                                 detalle.getCantidad(), cantidadPendiente)
                );
            }

            // Reducir cantidad del pedido
            Integer nuevaCantidad = detallePedido.getCantidad() - detalle.getCantidad();

            if (nuevaCantidad == 0) {
                // Soft delete del detalle
                detallePedido.setIsActive(false);
                detallePedido.setDeletionReason("Devolución completa - " + devolucion.getNumeroDevolucion());
                log.debug("DetallePedido {} eliminado por devolución completa", detallePedido.getId());
            } else {
                // Actualizar cantidad
                detallePedido.setCantidad(nuevaCantidad);
                // Recalcular subtotal
                detallePedido.setSubTotal(
                    detallePedido.getPrecioUnitario().multiply(BigDecimal.valueOf(nuevaCantidad))
                );
                log.debug("Cantidad de DetallePedido {} reducida a {}", detallePedido.getId(), nuevaCantidad);
            }

            detallePedidoRepository.save(detallePedido);
        }

        // Recalcular totales del pedido
        Pedido pedido = devolucion.getPedido();
        List<DetallePedido> detallesActivos = pedido.getDetalles().stream()
                .filter(BaseEntity::getIsActive)
                .toList();

        if (detallesActivos.isEmpty()) {
            pedido.setEstado(EstadoPedido.CANCELADO);
            log.info("Pedido {} cancelado por devolución total", pedido.getId());
        }
    }

    /**
     * Procesar corrección de factura (sin movimiento físico).
     */
    @Transactional
    protected void procesarCorreccionFactura(DevolucionVenta devolucion) {
        log.info("Procesando corrección de factura ID: {}", devolucion.getId());

        Factura factura = devolucion.getFactura();

        // Verificar si es anulación total
        boolean esAnulacionTotal = devolucion.getDetalles().stream()
                .allMatch(detalle -> {
                    if (detalle.getDetalleFactura() == null) return false;
                    return detalle.getCantidad().equals(detalle.getDetalleFactura().getCantidad());
                });

        if (esAnulacionTotal) {
            factura.anular();
            facturaRepository.save(factura);
            log.info("Factura {} anulada por devolución", factura.getId());
        }

        // Si hay detalles de pedido, revertir facturación
        for (DetalleDevolucion detalle : devolucion.getDetalles()) {
            if (detalle.getDetallePedido() != null) {
                DetallePedido detallePedido = detalle.getDetallePedido();
                detallePedido.revertirFacturacion(detalle.getCantidad());
                detallePedidoRepository.save(detallePedido);
            }
        }
    }

    /**
     * Generar nota de crédito automáticamente.
     */
    @Transactional
    protected void generarNotaCredito(DevolucionVenta devolucion) {
        log.info("Generando nota de crédito para devolución ID: {}", devolucion.getId());

        if (devolucion.getFactura() == null) {
            throw new BusinessException("No se puede generar nota de crédito sin factura original");
        }

        Factura facturaOriginal = devolucion.getFactura();

        // Crear nota de crédito
        Factura notaCredito = Factura.builder()
                .cliente(devolucion.getCliente())
                .pedido(devolucion.getPedido())
                .tipoFactura(TipoFactura.NOTA_CREDITO)
                .facturaOriginal(facturaOriginal)
                .devolucion(devolucion)
                .fechaEmision(LocalDateTime.now())
                .subtotal(devolucion.getSubtotal().doubleValue())
                .totalIva(devolucion.getTotalIva().doubleValue())
                .total(devolucion.getTotal().doubleValue())
                .estado(EstadoFactura.BORRADOR)
                .moneda("PYG")
                .observaciones("Nota de crédito por devolución " + devolucion.getNumeroDevolucion())
                .detalles(new ArrayList<>())
                .build();

        // Copiar detalles de devolución
        for (DetalleDevolucion detalleDevolucion : devolucion.getDetalles()) {
            DetalleFactura detalleNota = DetalleFactura.builder()
                    .factura(notaCredito)
                    .producto(detalleDevolucion.getProducto())
                    .codigo(detalleDevolucion.getProducto().getCodigo())
                    .descripcion(detalleDevolucion.getProducto().getDescripcion())
                    .cantidad(detalleDevolucion.getCantidad())
                    .precioUnitario(detalleDevolucion.getPrecioUnitario().doubleValue())
                    .porcentajeIva(detalleDevolucion.getPorcentajeIva())
                    .descuento(detalleDevolucion.getDescuento().doubleValue())
                    .build();

            notaCredito.getDetalles().add(detalleNota);
        }

        // Validar nota de crédito
        notaCredito.validarNotaCredito();

        // Guardar
        Factura savedNota = facturaRepository.save(notaCredito);

        // Asociar a devolución
        devolucion.setNotaCredito(savedNota);

        log.info("Nota de crédito {} generada para devolución {}", savedNota.getId(), devolucion.getId());
    }

    /**
     * Generar número de devolución único.
     */
    private String generarNumeroDevolucion() {
        int numero = devolucionCounter.getAndIncrement();
        return String.format("DEV-%06d", numero);
    }

    /**
     * Convertir entidad a DTO de respuesta.
     */
    private DevolucionResponse toResponse(DevolucionVenta devolucion) {
        List<DetalleDevolucionResponse> detallesResponse = devolucion.getDetalles() != null
                ? devolucion.getDetalles().stream()
                        .map(this::toDetalleResponse)
                        .toList()
                : new ArrayList<>();

        return DevolucionResponse.builder()
                .id(devolucion.getId())
                .numeroDevolucion(devolucion.getNumeroDevolucion())
                .tipo(devolucion.getTipo())
                .estado(devolucion.getEstado())
                .facturaId(devolucion.getFactura() != null ? devolucion.getFactura().getId() : null)
                .numeroFactura(devolucion.getFactura() != null ? devolucion.getFactura().getNumeroFactura() : null)
                .pedidoId(devolucion.getPedido() != null ? devolucion.getPedido().getId() : null)
                .codigoTracking(devolucion.getPedido() != null ? devolucion.getPedido().getCodigoTracking() : null)
                .clienteId(devolucion.getCliente().getId())
                .clienteNombre(devolucion.getCliente().getRazonSocial())
                .generarNotaCredito(devolucion.getGenerarNotaCredito())
                .notaCreditoId(devolucion.getNotaCredito() != null ? devolucion.getNotaCredito().getId() : null)
                .numeroNotaCredito(devolucion.getNotaCredito() != null ? devolucion.getNotaCredito().getNumeroFactura() : null)
                .fechaSolicitud(devolucion.getFechaSolicitud())
                .fechaAprobacion(devolucion.getFechaAprobacion())
                .fechaCompletada(devolucion.getFechaCompletada())
                .subtotal(devolucion.getSubtotal())
                .totalIva(devolucion.getTotalIva())
                .total(devolucion.getTotal())
                .motivo(devolucion.getMotivo())
                .observaciones(devolucion.getObservaciones())
                .aprobadoPorId(devolucion.getAprobadoPor() != null ? devolucion.getAprobadoPor().getId() : null)
                .aprobadoPorNombre(devolucion.getAprobadoPor() != null ? devolucion.getAprobadoPor().getNombre() : null)
                .detalles(detallesResponse)
                .createdAt(devolucion.getCreatedAt())
                .updatedAt(devolucion.getUpdatedAt())
                .build();
    }

    /**
     * Convertir detalle a DTO de respuesta.
     */
    private DetalleDevolucionResponse toDetalleResponse(DetalleDevolucion detalle) {
        return DetalleDevolucionResponse.builder()
                .id(detalle.getId())
                .productoId(detalle.getProducto().getId())
                .productoDescripcion(detalle.getProducto().getDescripcion())
                .productoCodigo(detalle.getProducto().getCodigo())
                .detalleFacturaId(detalle.getDetalleFactura() != null ? detalle.getDetalleFactura().getId() : null)
                .detallePedidoId(detalle.getDetallePedido() != null ? detalle.getDetallePedido().getId() : null)
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .descuento(detalle.getDescuento())
                .subtotal(detalle.getSubtotal())
                .porcentajeIva(detalle.getPorcentajeIva())
                .montoIva(detalle.getMontoIva())
                .total(detalle.getTotal())
                .estadoProducto(detalle.getEstadoProducto())
                .inventarioEntradaId(detalle.getInventarioEntrada() != null ? detalle.getInventarioEntrada().getId() : null)
                .observaciones(detalle.getObservaciones())
                .build();
    }
}
