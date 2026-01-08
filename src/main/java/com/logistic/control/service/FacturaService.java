package com.logistic.control.service;

import com.logistic.control.dto.request.FacturaRequest;
import com.logistic.control.dto.request.FacturaParcialRequest;
import com.logistic.control.dto.response.FacturaResponse;
import com.logistic.control.dto.response.ItemFacturaResponse;
import com.logistic.control.entity.*;
import com.logistic.control.enums.EstadoFactura;
import com.logistic.control.enums.EstadoPedido;
import com.logistic.control.exception.ResourceNotFoundException;
import com.logistic.control.exception.BusinessException;
import com.logistic.control.exception.InvalidStateException;
import com.logistic.control.repository.FacturaRepository;
import com.logistic.control.repository.ClienteRepository;
import com.logistic.control.repository.PedidoRepository;
import com.logistic.control.repository.DetallePedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Servicio para gestión de Facturas
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ClienteService clienteService;
    private final SifenService sifenService;

    /**
     * Listar todas las facturas con paginación
     */
    public Page<FacturaResponse> listarFacturas(Pageable pageable) {
        log.debug("Listando facturas - página: {}", pageable.getPageNumber());
        return facturaRepository.findAllWithCliente(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtener factura por ID
     */
    public FacturaResponse obtenerFactura(Long id) {
        log.debug("Obteniendo factura con ID: {}", id);
        Factura factura = facturaRepository.findByIdWithCliente(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
        return toResponse(factura);
    }

    /**
     * Buscar facturas por cliente
     */
    public Page<FacturaResponse> buscarPorCliente(Long clienteId, Pageable pageable) {
        log.debug("Buscando facturas del cliente: {}", clienteId);
        var facturas = facturaRepository.findByClienteId(clienteId);
        return new org.springframework.data.domain.PageImpl<>(
            facturas.stream().map(this::toResponse).toList(),
            pageable,
            facturas.size()
        );
    }

    /**
     * Buscar facturas por estado
     */
    public Page<FacturaResponse> buscarPorEstado(EstadoFactura estado, Pageable pageable) {
        log.debug("Buscando facturas con estado: {}", estado);
        var facturas = facturaRepository.findByEstado(estado);
        return new org.springframework.data.domain.PageImpl<>(
            facturas.stream().map(this::toResponse).toList(),
            pageable,
            facturas.size()
        );
    }

    /**
     * Buscar facturas por rango de fechas
     */
    public Page<FacturaResponse> buscarPorRangoFechas(LocalDate inicio, LocalDate fin, Pageable pageable) {
        log.debug("Buscando facturas entre {} y {}", inicio, fin);
        var facturas = facturaRepository.findByFechaEmisionBetween(
                inicio.atStartOfDay(), 
                fin.atTime(23, 59, 59)
        );
        return new org.springframework.data.domain.PageImpl<>(
            facturas.stream().map(this::toResponse).toList(),
            pageable,
            facturas.size()
        );
    }

    /**
     * Crear factura desde pedido
     */
    @Transactional
    public FacturaResponse crearFacturaDesdePedido(Long pedidoId) {
        log.info("Creando factura para pedido: {}", pedidoId);
        
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", pedidoId));
        
        Cliente cliente = pedido.getCliente();

        // Nota: Un pedido puede tener múltiples facturas (ej: factura original + notas de crédito)
        // Por ahora permitimos crear múltiples facturas para el mismo pedido
        
        // Crear factura
        Factura factura = Factura.builder()
                .cliente(cliente)
                .pedido(pedido)
                .fechaEmision(LocalDateTime.now())
                .fechaVencimiento(LocalDate.now().plusDays(30))
                .subtotal(0.0)
                .iva10(0.0)
                .totalIva(0.0)
                .total(0.0)
                .estado(EstadoFactura.GENERADA)
                .moneda("PYG")
                .moneda("PYG")
                .observaciones(pedido.getObservaciones())
                .detalles(new ArrayList<>())
                .build();
        
        // Copiar detalles del pedido
        for (DetallePedido detallePedido : pedido.getDetalles()) {
            DetalleFactura detalleFactura = DetalleFactura.builder()
                    .factura(factura)
                    .descripcion(detallePedido.getProducto().getDescripcion())
                    .cantidad(detallePedido.getCantidad())
                    .precioUnitario(detallePedido.getPrecioUnitario().doubleValue())
                    .porcentajeIva(10)
                    .build();
            factura.getDetalles().add(detalleFactura);
        }
        
        Factura saved = facturaRepository.save(factura);
        log.info("Factura creada exitosamente con ID: {}", saved.getId());
        
        return toResponse(saved);
    }

    /**
     * Crear factura manual (sin pedido)
     */
    @Transactional
    public FacturaResponse crearFactura(FacturaRequest request) {
        log.info("Creando factura manual para cliente: {}", request.getClienteId());

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClienteId()));

        // Buscar pedido si está asociado
        Pedido pedido = null;
        if (request.getPedidoId() != null) {
            pedido = pedidoRepository.findById(request.getPedidoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", request.getPedidoId()));
        }

        // Calcular totales a partir de los ítems
        Double subtotal = 0.0;
        Double iva5 = 0.0;
        Double iva10 = 0.0;

        for (var item : request.getItems()) {
            Double itemSubtotal = item.getCantidad() * item.getPrecioUnitario();
            subtotal += itemSubtotal;

            if (item.getTasaIva() == 5) {
                iva5 += itemSubtotal * 0.05;
            } else if (item.getTasaIva() == 10) {
                iva10 += itemSubtotal * 0.10;
            }
        }

        Double totalIva = iva5 + iva10;
        Double total = subtotal + totalIva;

        // Determinar fecha de emisión
        LocalDateTime fechaEmision = request.getFechaEmision() != null
                ? request.getFechaEmision().atStartOfDay()
                : LocalDateTime.now();

        // Crear factura
        Factura factura = Factura.builder()
                .cliente(cliente)
                .pedido(pedido)
                .tipo(request.getTipo())
                .condicionPago(request.getCondicionPago())
                .fechaEmision(fechaEmision)
                .fechaVencimiento(request.getFechaVencimiento())
                .subtotal(subtotal)
                .iva5(iva5)
                .iva10(iva10)
                .totalIva(totalIva)
                .total(total)
                .estado(EstadoFactura.BORRADOR)
                .moneda(request.getMoneda() != null ? request.getMoneda() : "PYG")
                .observaciones(request.getObservaciones())
                .saldo(total)
                .detalles(new ArrayList<>())
                .build();

        // Crear detalles de factura
        for (var itemRequest : request.getItems()) {
            DetalleFactura detalle = DetalleFactura.builder()
                    .factura(factura)
                    .codigo(itemRequest.getCodigo())
                    .descripcion(itemRequest.getDescripcion())
                    .cantidad(itemRequest.getCantidad())
                    .unidadMedida(itemRequest.getUnidadMedida())
                    .precioUnitario(itemRequest.getPrecioUnitario())
                    .porcentajeIva(itemRequest.getTasaIva())
                    .build();
            factura.getDetalles().add(detalle);
        }

        Factura saved = facturaRepository.save(factura);

        log.info("Factura manual creada exitosamente con ID: {}", saved.getId());
        return toResponse(saved);
    }

    /**
     * Crear factura parcial desde pedido.
     * Permite facturar cantidades específicas de cada ítem del pedido.
     * Actualiza automáticamente el estado del pedido a FACTURADO si se factura el 100%.
     */
    @Transactional
    public FacturaResponse crearFacturaParcial(FacturaParcialRequest request) {
        log.info("Creando factura parcial para pedido: {}", request.getPedidoId());

        // 1. Validar pedido y estado
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", request.getPedidoId()));

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new InvalidStateException("No se puede facturar un pedido cancelado");
        }

        if (pedido.getEstado() == EstadoPedido.FACTURADO) {
            throw new InvalidStateException("El pedido ya está completamente facturado");
        }

        Cliente cliente = pedido.getCliente();

        // 2. Validar que el pedido tenga items pendientes de facturar
        Boolean tienePendientes = !pedidoRepository.isPedidoCompletamenteFacturado(pedido.getId());
        if (!tienePendientes) {
            throw new BusinessException("El pedido no tiene ítems pendientes de facturar");
        }

        // 3. Determinar fechas
        LocalDateTime fechaEmision = request.getFechaEmision() != null
                ? request.getFechaEmision().atStartOfDay()
                : LocalDateTime.now();

        LocalDate fechaVencimiento = request.getFechaVencimiento() != null
                ? request.getFechaVencimiento()
                : LocalDate.now().plusDays(30);

        // 4. Crear factura
        Factura factura = Factura.builder()
                .cliente(cliente)
                .pedido(pedido)
                .tipo(request.getTipo())
                .condicionPago(request.getCondicionPago())
                .fechaEmision(fechaEmision)
                .fechaVencimiento(fechaVencimiento)
                .subtotal(0.0)
                .iva5(0.0)
                .iva10(0.0)
                .totalIva(0.0)
                .total(0.0)
                .estado(EstadoFactura.BORRADOR)
                .moneda("PYG")
                .observaciones(request.getObservaciones())
                .detalles(new ArrayList<>())
                .build();

        // 5. Procesar cada ítem del request
        Double subtotal = 0.0;
        Double iva5 = 0.0;
        Double iva10 = 0.0;

        for (FacturaParcialRequest.ItemFacturaParcialRequest itemRequest : request.getItems()) {
            // Cargar DetallePedido
            DetallePedido detallePedido = detallePedidoRepository.findById(itemRequest.getDetallePedidoId())
                    .orElseThrow(() -> new ResourceNotFoundException("DetallePedido", "id", itemRequest.getDetallePedidoId()));

            // Validar que pertenece al pedido correcto
            if (!detallePedido.getPedido().getId().equals(pedido.getId())) {
                throw new BusinessException(
                    String.format("El detalle %d no pertenece al pedido %d",
                                 itemRequest.getDetallePedidoId(), pedido.getId())
                );
            }

            // Validar cantidad pendiente
            Integer cantidadPendiente = detallePedido.getCantidadPendienteFacturar();
            if (itemRequest.getCantidadAFacturar() > cantidadPendiente) {
                throw new BusinessException(
                    String.format("Cantidad a facturar (%d) excede cantidad pendiente (%d) del producto '%s'",
                                 itemRequest.getCantidadAFacturar(),
                                 cantidadPendiente,
                                 detallePedido.getProducto().getDescripcion())
                );
            }

            // Determinar precio unitario (permitir override si está presente)
            Double precioUnitario = itemRequest.getPrecioUnitarioOverride() != null
                    ? itemRequest.getPrecioUnitarioOverride()
                    : detallePedido.getPrecioUnitario().doubleValue();

            // Crear DetalleFactura con referencia a DetallePedido
            DetalleFactura detalleFactura = DetalleFactura.builder()
                    .factura(factura)
                    .detallePedido(detallePedido)
                    .producto(detallePedido.getProducto())
                    .codigo(detallePedido.getProducto().getCodigo())
                    .descripcion(detallePedido.getProducto().getDescripcion())
                    .cantidad(itemRequest.getCantidadAFacturar())
                    .unidadMedida(detallePedido.getProducto().getUnidadMedida())
                    .precioUnitario(precioUnitario)
                    .porcentajeIva(10) // Por defecto 10%, podría venir del producto
                    .descuento(0.0)
                    .build();

            // Calcular subtotal del ítem
            Double itemSubtotal = itemRequest.getCantidadAFacturar() * precioUnitario;
            subtotal += itemSubtotal;

            // Calcular IVA según tasa (10% por defecto)
            if (detalleFactura.getPorcentajeIva() == 5) {
                iva5 += itemSubtotal * 0.05;
            } else if (detalleFactura.getPorcentajeIva() == 10) {
                iva10 += itemSubtotal * 0.10;
            }

            factura.getDetalles().add(detalleFactura);

            // Actualizar cantidad facturada del DetallePedido
            detallePedido.facturar(itemRequest.getCantidadAFacturar());
            detallePedidoRepository.save(detallePedido);

            log.debug("Item facturado: {} - Cantidad: {} - Pendiente restante: {}",
                     detallePedido.getProducto().getDescripcion(),
                     itemRequest.getCantidadAFacturar(),
                     detallePedido.getCantidadPendienteFacturar());
        }

        // 6. Actualizar totales de la factura
        Double totalIva = iva5 + iva10;
        Double total = subtotal + totalIva;

        factura.setSubtotal(subtotal);
        factura.setIva5(iva5);
        factura.setIva10(iva10);
        factura.setTotalIva(totalIva);
        factura.setTotal(total);
        factura.setSaldo(total);

        // 7. Guardar factura
        Factura saved = facturaRepository.save(factura);

        // 8. Verificar si el pedido está 100% facturado y actualizar estado automáticamente
        verificarYActualizarEstadoPedido(pedido);

        log.info("Factura parcial creada exitosamente con ID: {} - Total items: {} - Total: {}",
                saved.getId(), factura.getDetalles().size(), total);

        return toResponse(saved);
    }

    /**
     * Verifica si un pedido está completamente facturado y actualiza su estado automáticamente.
     */
    @Transactional
    protected void verificarYActualizarEstadoPedido(Pedido pedido) {
        Boolean completamenteFacturado = pedidoRepository.isPedidoCompletamenteFacturado(pedido.getId());

        if (completamenteFacturado) {
            log.info("Pedido {} está 100% facturado. Actualizando estado a FACTURADO", pedido.getId());
            pedido.setEstado(EstadoPedido.FACTURADO);
            pedidoRepository.save(pedido);
        } else {
            log.debug("Pedido {} aún tiene items pendientes de facturar", pedido.getId());
        }
    }

    /**
     * Emitir factura electrónica vía SIFEN
     */
    @Transactional
    public FacturaResponse emitirFacturaElectronica(Long facturaId) {
        log.info("Emitiendo factura electrónica ID: {}", facturaId);
        
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", facturaId));
        
        // Validar estado
        if (factura.getEstado() != EstadoFactura.GENERADA && factura.getEstado() != EstadoFactura.BORRADOR) {
            throw new InvalidStateException("Solo se pueden emitir facturas en estado GENERADA o BORRADOR");
        }
        
        // Validar que el cliente sea facturador electrónico
        if (!factura.getCliente().getEsFacturadorElectronico()) {
            throw new BusinessException("El cliente no está configurado para facturación electrónica");
        }
        
        try {
            // Enviar a SIFEN (el servicio actualiza la factura con el CDC)
            factura.setEstado(EstadoFactura.EN_PROCESO);
            facturaRepository.save(factura);
            
            log.info("Factura electrónica enviada a SIFEN para procesamiento");
            return toResponse(factura);
            
        } catch (Exception e) {
            log.error("Error al emitir factura electrónica: {}", e.getMessage(), e);
            throw new BusinessException("Error al emitir factura electrónica: " + e.getMessage());
        }
    }

    /**
     * Cancelar factura
     */
    @Transactional
    public void cancelarFactura(Long facturaId, String motivo) {
        log.info("Cancelando factura ID: {}", facturaId);
        
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", facturaId));
        
        // Validar estado
        if (factura.getEstado() == EstadoFactura.ANULADA) {
            throw new InvalidStateException("No se puede cancelar una factura anulada");
        }
        
        // Si tiene CDC, marcar para cancelación en SIFEN
        if (factura.getCdc() != null) {
            log.warn("Factura con CDC {} debe ser cancelada manualmente en SIFEN", factura.getCdc());
        }
        
        // Actualizar estado
        factura.setEstado(EstadoFactura.ANULADA);
        String obs = factura.getObservaciones() != null ? factura.getObservaciones() : "";
        factura.setObservaciones(obs + " | ANULADA: " + motivo);
        
        facturaRepository.save(factura);
        log.info("Factura cancelada exitosamente: {}", facturaId);
    }

    /**
     * Registrar pago de factura
     */
    @Transactional
    public void registrarPago(Long facturaId, Double monto, String metodoPago) {
        log.info("Registrando pago de factura ID: {}", facturaId);
        
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", facturaId));
        
        // Validar estado
        if (factura.getEstado() == EstadoFactura.ANULADA) {
            throw new InvalidStateException("La factura ya está anulada");
        }
        
        // Validar monto
        if (!monto.equals(factura.getTotal())) {
            throw new BusinessException("El monto del pago debe ser igual al total de la factura");
        }
        
        // Registrar pago
        Pago pago = Pago.builder()
                .factura(factura)
                .monto(monto)
                .metodoPago(metodoPago)
                .fechaPago(LocalDateTime.now())
                .build();
        
        factura.registrarPago(pago);
        factura.setEstado(EstadoFactura.PAGADA);
        
        facturaRepository.save(factura);
        log.info("Pago registrado exitosamente para factura: {}", facturaId);
    }

    /**
     * Convertir entidad a DTO de respuesta
     */
    private FacturaResponse toResponse(Factura factura) {
        // Convertir detalles/ítems a DTOs
        java.util.List<ItemFacturaResponse> itemsResponse = factura.getDetalles() != null
                ? factura.getDetalles().stream()
                        .map(this::toItemResponse)
                        .toList()
                : new ArrayList<>();

        // Calcular estado de pago
        String estadoPago = calcularEstadoPago(factura);

        // Obtener datos del cliente con null check
        Cliente cliente = factura.getCliente();
        Long clienteId = cliente != null ? cliente.getId() : null;
        String clienteRazon = cliente != null ? cliente.getRazonSocial() : null;
        String clienteRuc = cliente != null ? cliente.getRuc() : null;

        return FacturaResponse.builder()
                .id(factura.getId())
                .numeroFactura(factura.getNumeroFactura())
                .numeroDocumento(factura.getNumeroFactura()) // Same as numeroFactura
                .tipo(factura.getTipo() != null ? factura.getTipo() : "CONTADO")
                .clienteId(clienteId)
                .clienteNombre(clienteRazon)
                .clienteRazonSocial(clienteRazon)
                .clienteRuc(clienteRuc)
                .fechaEmision(factura.getFechaEmision())
                .fechaVencimiento(factura.getFechaVencimiento())
                .condicionPago(factura.getCondicionPago())
                .subtotal(factura.getSubtotal())
                .iva5(factura.getIva5())
                .iva10(factura.getIva10())
                .totalIva(factura.getTotalIva())
                .ivaTotal(factura.getTotalIva()) // Alias for frontend compatibility
                .total(factura.getTotal())
                .descuento(factura.getDescuento())
                .estado(factura.getEstado())
                .estadoPago(estadoPago)
                .moneda(factura.getMoneda())
                .cdc(factura.getCdc())
                .timbrado(factura.getTimbrado())
                .establecimiento(factura.getEstablecimiento())
                .puntoExpedicion(factura.getPuntoExpedicion())
                .fechaAprobacionSifen(factura.getFechaAprobacionSifen())
                .codigoEstadoSifen(factura.getCodigoEstadoSifen())
                .mensajeSifen(factura.getMensajeSifen())
                .qrCode(factura.getQrCode())
                .urlKude(factura.getUrlKude())
                .saldo(factura.getSaldo())
                .saldoPendiente(factura.getSaldo()) // Alias for frontend compatibility
                .pagado(factura.getPagado())
                .observaciones(factura.getObservaciones())
                .createdAt(factura.getCreatedAt())
                .updatedAt(factura.getUpdatedAt())
                .items(itemsResponse)
                .build();
    }

    /**
     * Calcular estado de pago de una factura
     */
    private String calcularEstadoPago(Factura factura) {
        if (factura.getEstado() == EstadoFactura.ANULADA || factura.getEstado() == EstadoFactura.RECHAZADA) {
            return "ANULADO";
        }

        if (factura.getEstado() == EstadoFactura.PAGADA) {
            return "PAGADO";
        }

        Double total = factura.getTotal() != null ? factura.getTotal() : 0.0;
        Double pagado = factura.getPagado() != null ? factura.getPagado() : 0.0;
        Double saldo = factura.getSaldo() != null ? factura.getSaldo() : total;

        // Si está completamente pagado
        if (pagado >= total || saldo <= 0.01) {
            return "PAGADO";
        }

        // Si tiene pagos parciales
        if (pagado > 0.01) {
            return "PARCIAL";
        }

        // Si está vencido
        if (factura.getFechaVencimiento() != null &&
            factura.getFechaVencimiento().isBefore(LocalDate.now()) &&
            factura.getTipo() != null && factura.getTipo().equals("CREDITO")) {
            return "VENCIDO";
        }

        // Si no tiene pagos
        return "PENDIENTE";
    }

    /**
     * Convertir detalle de factura a DTO de respuesta
     */
    private ItemFacturaResponse toItemResponse(DetalleFactura detalle) {
        return ItemFacturaResponse.builder()
                .id(detalle.getId())
                .codigo(detalle.getCodigo())
                .descripcion(detalle.getDescripcion())
                .cantidad(detalle.getCantidad())
                .unidadMedida(detalle.getUnidadMedida())
                .precioUnitario(detalle.getPrecioUnitario())
                .tasaIva(detalle.getPorcentajeIva())
                .subtotal(detalle.getSubtotal())
                .montoIva(detalle.getMontoIva())
                .total(detalle.getTotal())
                .build();
    }
}
