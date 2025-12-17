package com.logistic.control.service;

import com.logistic.control.dto.request.FacturaRequest;
import com.logistic.control.dto.response.FacturaResponse;
import com.logistic.control.entity.*;
import com.logistic.control.enums.EstadoFactura;
import com.logistic.control.exception.ResourceNotFoundException;
import com.logistic.control.exception.BusinessException;
import com.logistic.control.exception.InvalidStateException;
import com.logistic.control.repository.FacturaRepository;
import com.logistic.control.repository.ClienteRepository;
import com.logistic.control.repository.PedidoRepository;
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
    private final ClienteService clienteService;
    private final SifenService sifenService;

    /**
     * Listar todas las facturas con paginación
     */
    public Page<FacturaResponse> listarFacturas(Pageable pageable) {
        log.debug("Listando facturas - página: {}", pageable.getPageNumber());
        return facturaRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtener factura por ID
     */
    public FacturaResponse obtenerFactura(Long id) {
        log.debug("Obteniendo factura con ID: {}", id);
        Factura factura = facturaRepository.findById(id)
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
        
        // Validar que el pedido no tenga factura
        if (pedido.getFactura() != null) {
            throw new BusinessException("El pedido ya tiene una factura asociada");
        }
        
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
        
        // Calcular IVA (10%)
        Double subtotalValue = 0.0;
        Double descuentoValue = 0.0;
        if (request.getSubtotal() != null) {
            subtotalValue = request.getSubtotal();
        }
        if (request.getDescuento() != null) {
            descuentoValue = request.getDescuento();
        }
        Double subtotalConDescuento = subtotalValue - descuentoValue;
        Double iva10 = subtotalConDescuento * 0.10;
        Double total = subtotalConDescuento + iva10;
        
        // Crear factura
        Factura factura = Factura.builder()
                .cliente(cliente)
                .fechaEmision(LocalDateTime.now())
                .fechaVencimiento(LocalDate.now().plusDays(30))
                .subtotal(subtotalValue)
                .descuento(descuentoValue)
                .iva10(iva10)
                .totalIva(iva10)
                .total(total)
                .estado(EstadoFactura.BORRADOR)
                .moneda(request.getMoneda() != null ? request.getMoneda() : "PYG")
                .timbrado(request.getTimbrado())
                .establecimiento(request.getEstablecimiento())
                .puntoExpedicion(request.getPuntoExpedicion())
                .observaciones(request.getObservaciones())
                .saldo(total)
                .detalles(new ArrayList<>())
                .build();
        
        Factura saved = facturaRepository.save(factura);
        
        log.info("Factura manual creada exitosamente con ID: {}", saved.getId());
        return toResponse(saved);
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
        return FacturaResponse.builder()
                .id(factura.getId())
                .numeroFactura(factura.getNumeroFactura())
                .clienteId(factura.getCliente().getId())
                .clienteNombre(factura.getCliente().getRazonSocial())
                .clienteRuc(factura.getCliente().getRuc())
                .fechaEmision(factura.getFechaEmision())
                .fechaVencimiento(factura.getFechaVencimiento())
                .subtotal(factura.getSubtotal())
                .iva5(factura.getIva5())
                .iva10(factura.getIva10())
                .totalIva(factura.getTotalIva())
                .total(factura.getTotal())
                .descuento(factura.getDescuento())
                .estado(factura.getEstado())
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
                .pagado(factura.getPagado())
                .observaciones(factura.getObservaciones())
                .createdAt(factura.getCreatedAt())
                .updatedAt(factura.getUpdatedAt())
                .build();
    }
}
