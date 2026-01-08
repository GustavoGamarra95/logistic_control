package com.logistic.control.controller;

import com.logistic.control.dto.request.FacturaRequest;
import com.logistic.control.dto.request.FacturaParcialRequest;
import com.logistic.control.dto.request.SifenDocumentoRequest;
import com.logistic.control.dto.response.*;
import com.logistic.control.entity.Cliente;
import com.logistic.control.entity.DetallePedido;
import com.logistic.control.entity.Factura;
import com.logistic.control.entity.Pedido;
import com.logistic.control.enums.EstadoFactura;
import com.logistic.control.repository.ClienteRepository;
import com.logistic.control.repository.FacturaRepository;
import com.logistic.control.repository.PedidoRepository;
import com.logistic.control.service.FacturaService;
import com.logistic.control.service.SifenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller para gestión de Facturas (SIFEN)
 */
@RestController
@RequestMapping("/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final FacturaService facturaService;
    private final SifenService sifenService;

    @GetMapping
    public ResponseEntity<Page<FacturaResponse>> listarFacturas(Pageable pageable) {
        Page<Factura> facturas = facturaRepository.findAll(pageable);
        Page<FacturaResponse> response = facturas.map(this::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponse> obtenerFactura(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(factura -> ResponseEntity.ok(toResponse(factura)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<FacturaResponse> buscarPorNumero(@PathVariable String numero) {
        return facturaRepository.findByNumeroFactura(numero)
                .map(factura -> ResponseEntity.ok(toResponse(factura)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cdc/{cdc}")
    public ResponseEntity<FacturaResponse> buscarPorCdc(@PathVariable String cdc) {
        return facturaRepository.findByCdc(cdc)
                .map(factura -> ResponseEntity.ok(toResponse(factura)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<FacturaResponse>> listarPorCliente(@PathVariable Long clienteId) {
        List<Factura> facturas = facturaRepository.findByClienteId(clienteId);
        List<FacturaResponse> response = facturas.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<FacturaResponse>> listarPorEstado(@PathVariable EstadoFactura estado) {
        List<Factura> facturas = facturaRepository.findByEstado(estado);
        List<FacturaResponse> response = facturas.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<FacturaResponse>> listarPendientes() {
        List<Factura> facturas = facturaRepository.findByEstado(EstadoFactura.GENERADA);
        List<FacturaResponse> response = facturas.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vencidas")
    public ResponseEntity<List<FacturaResponse>> listarVencidas() {
        List<Factura> facturas = facturaRepository.findFacturasVencidas(LocalDate.now());
        List<FacturaResponse> response = facturas.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rango-fecha")
    public ResponseEntity<List<FacturaResponse>> listarPorRangoFecha(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {
        List<Factura> facturas = facturaRepository.findByFechaEmisionBetween(
                desde.atStartOfDay(),
                hasta.atTime(23, 59, 59));
        List<FacturaResponse> response = facturas.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<FacturaResponse> crearFactura(@Valid @RequestBody FacturaRequest request) {
        FacturaResponse factura = facturaService.crearFactura(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(factura);
    }

    /**
     * Crear factura parcial desde pedido.
     * Permite facturar cantidades específicas de cada ítem del pedido.
     */
    @PostMapping("/parcial")
    public ResponseEntity<FacturaResponse> crearFacturaParcial(
            @Valid @RequestBody FacturaParcialRequest request) {
        FacturaResponse factura = facturaService.crearFacturaParcial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(factura);
    }

    /**
     * Listar pedidos que tienen al menos un ítem pendiente de facturar.
     * Útil para mostrar en el módulo de facturación.
     */
    @GetMapping("/pedidos-pendientes")
    public ResponseEntity<List<PedidoResponse>> listarPedidosPendientesDeFacturar() {
        List<Pedido> pedidos = pedidoRepository.findPedidosPendientesDeFacturar();
        List<PedidoResponse> response = pedidos.stream()
                .map(this::toPedidoResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener información de facturación de un pedido.
     * Incluye porcentaje facturado y cantidad pendiente por ítem.
     */
    @GetMapping("/pedidos/{pedidoId}/facturacion")
    public ResponseEntity<Map<String, Object>> obtenerInfoFacturacionPedido(
            @PathVariable Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Boolean completamenteFacturado = pedidoRepository.isPedidoCompletamenteFacturado(pedidoId);

        // Calcular totales
        int totalItems = pedido.getDetalles().size();
        int itemsCompletamenteFacturados = (int) pedido.getDetalles().stream()
                .filter(DetallePedido::estaCompletamenteFacturado)
                .count();

        int cantidadTotal = pedido.getDetalles().stream()
                .mapToInt(DetallePedido::getCantidad)
                .sum();

        int cantidadFacturada = pedido.getDetalles().stream()
                .mapToInt(d -> d.getCantidadFacturada() != null ? d.getCantidadFacturada() : 0)
                .sum();

        int cantidadPendiente = cantidadTotal - cantidadFacturada;

        double porcentajeFacturado = cantidadTotal > 0
                ? (cantidadFacturada * 100.0 / cantidadTotal)
                : 0.0;

        // Construir respuesta
        Map<String, Object> info = new HashMap<>();
        info.put("pedidoId", pedidoId);
        info.put("completamenteFacturado", completamenteFacturado);
        info.put("porcentajeFacturado", Math.round(porcentajeFacturado * 100.0) / 100.0);
        info.put("cantidadTotal", cantidadTotal);
        info.put("cantidadFacturada", cantidadFacturada);
        info.put("cantidadPendiente", cantidadPendiente);
        info.put("totalItems", totalItems);
        info.put("itemsCompletamenteFacturados", itemsCompletamenteFacturados);
        info.put("itemsPendientes", totalItems - itemsCompletamenteFacturados);

        // Detalle por ítem
        List<Map<String, Object>> detalleItems = pedido.getDetalles().stream()
                .map(detalle -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("detallePedidoId", detalle.getId());
                    item.put("productoId", detalle.getProducto().getId());
                    item.put("productoDescripcion", detalle.getProducto().getDescripcion());
                    item.put("cantidad", detalle.getCantidad());
                    item.put("cantidadFacturada", detalle.getCantidadFacturada());
                    item.put("cantidadPendiente", detalle.getCantidadPendienteFacturar());
                    item.put("porcentajeFacturado", Math.round(detalle.getPorcentajeFacturado() * 100.0) / 100.0);
                    item.put("completamenteFacturado", detalle.estaCompletamenteFacturado());
                    return item;
                })
                .collect(Collectors.toList());

        info.put("detalles", detalleItems);

        return ResponseEntity.ok(info);
    }

    // Actualización de facturas deshabilitada temporalmente - las facturas se generan completas
    // @PutMapping("/{id}")
    // public ResponseEntity<FacturaResponse> actualizarFactura(
    //         @PathVariable Long id,
    //         @Valid @RequestBody FacturaRequest request) {
    //     // TODO: Implementar actualización con ítems
    //     throw new UnsupportedOperationException("La actualización de facturas aún no está implementada");
    // }

    @PatchMapping("/{id}/calcular-totales")
    public ResponseEntity<FacturaResponse> calcularTotales(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(factura -> {
                    factura.calcularTotales();
                    Factura updated = facturaRepository.save(factura);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/aprobar-sifen")
    public ResponseEntity<FacturaResponse> aprobarSifen(
            @PathVariable Long id,
            @RequestParam String cdc,
            @RequestParam String respuesta) {
        return facturaRepository.findById(id)
                .map(factura -> {
                    factura.aprobarSifen(cdc, respuesta);
                    Factura updated = facturaRepository.save(factura);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/rechazar-sifen")
    public ResponseEntity<FacturaResponse> rechazarSifen(
            @PathVariable Long id,
            @RequestParam String codigo,
            @RequestParam String mensaje) {
        return facturaRepository.findById(id)
                .map(factura -> {
                    factura.rechazarSifen(codigo, mensaje);
                    Factura updated = facturaRepository.save(factura);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<FacturaResponse> anular(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(factura -> {
                    factura.anular();
                    Factura updated = facturaRepository.save(factura);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (factura.getEstado() == EstadoFactura.APROBADA) {
            throw new RuntimeException("No se puede eliminar una factura aprobada");
        }

        facturaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ============= ENDPOINTS SIFEN =============

    /**
     * Envía una factura a SIFEN para su procesamiento
     */
    @PostMapping("/{id}/enviar-sifen")
    public ResponseEntity<SifenResponse> enviarASifen(
            @PathVariable Long id,
            @RequestBody(required = false) SifenDocumentoRequest request) {
        
        if (request == null) {
            request = new SifenDocumentoRequest();
            request.setFacturaId(id);
        }
        
        SifenResponse response = sifenService.enviarFacturaASifen(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Envía múltiples facturas en lote a SIFEN
     */
    @PostMapping("/lote/enviar-sifen")
    public ResponseEntity<SifenLoteResponse> enviarLoteASifen(@RequestBody List<Long> facturasIds) {
        SifenLoteResponse response = sifenService.enviarLoteASifen(facturasIds);
        return ResponseEntity.ok(response);
    }

    /**
     * Consulta el estado de una factura en SIFEN por CDC
     */
    @GetMapping("/consultar-sifen/{cdc}")
    public ResponseEntity<SifenConsultaResponse> consultarEstadoSifen(@PathVariable String cdc) {
        SifenConsultaResponse response = sifenService.consultarEstadoFactura(cdc);
        return ResponseEntity.ok(response);
    }

    /**
     * Consulta el estado de un lote en SIFEN
     */
    @GetMapping("/lote/consultar-sifen/{numeroLote}")
    public ResponseEntity<SifenLoteResponse> consultarLoteSifen(@PathVariable String numeroLote) {
        SifenLoteResponse response = sifenService.consultarEstadoLote(numeroLote);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza el estado de una factura consultando en SIFEN
     */
    @PostMapping("/{id}/actualizar-estado-sifen")
    public ResponseEntity<FacturaResponse> actualizarEstadoSifen(@PathVariable Long id) {
        Factura factura = sifenService.actualizarEstadoDesdeConsulta(id);
        return ResponseEntity.ok(toResponse(factura));
    }

    /**
     * Obtiene el XML generado de una factura
     */
    @GetMapping("/{id}/xml")
    public ResponseEntity<String> obtenerXml(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(factura -> ResponseEntity.ok(factura.getXmlDe()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene el XML firmado de una factura
     */
    @GetMapping("/{id}/xml-firmado")
    public ResponseEntity<String> obtenerXmlFirmado(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(factura -> ResponseEntity.ok(factura.getXmlDeFirmado()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene el código QR de una factura
     */
    @GetMapping("/{id}/qr")
    public ResponseEntity<String> obtenerQr(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(factura -> {
                    if (factura.getQrCode() == null || factura.getQrCode().isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Factura no tiene código QR generado");
                    }
                    return ResponseEntity.ok(factura.getQrCode());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene la URL de KuDE de una factura
     */
    @GetMapping("/{id}/kude")
    public ResponseEntity<String> obtenerKudeUrl(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(factura -> {
                    if (factura.getUrlKude() == null || factura.getUrlKude().isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Factura no tiene URL de KuDE");
                    }
                    return ResponseEntity.ok(factura.getUrlKude());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Regenera el XML de una factura
     */
    @PostMapping("/{id}/regenerar-xml")
    public ResponseEntity<FacturaResponse> regenerarXml(@PathVariable Long id) {
        Factura factura = sifenService.regenerarXml(id);
        return ResponseEntity.ok(toResponse(factura));
    }

    /**
     * Valida la firma digital de una factura
     */
    @GetMapping("/{id}/validar-firma")
    public ResponseEntity<Boolean> validarFirma(@PathVariable Long id) {
        boolean esValida = sifenService.validarFirmaDigital(id);
        return ResponseEntity.ok(esValida);
    }

    // ============= FIN ENDPOINTS SIFEN =============

    private FacturaResponse toResponse(Factura factura) {
        return FacturaResponse.builder()
                .id(factura.getId())
                .numeroFactura(factura.getNumeroFactura())
                .fechaEmision(factura.getFechaEmision())
                .fechaVencimiento(factura.getFechaVencimiento())
                .clienteId(factura.getCliente().getId())
                .clienteNombre(factura.getCliente().getRazonSocial())
                .clienteRuc(factura.getCliente().getRuc())
                .subtotal(factura.getSubtotal())
                .iva5(factura.getIva5())
                .iva10(factura.getIva10())
                .totalIva(factura.getTotalIva())
                .total(factura.getTotal())
                .descuento(factura.getDescuento())
                .moneda(factura.getMoneda())
                .estado(factura.getEstado())
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

    /**
     * Helper para convertir Pedido a PedidoResponse.
     * Usado para listar pedidos pendientes de facturar.
     */
    private PedidoResponse toPedidoResponse(Pedido pedido) {
        return PedidoResponse.builder()
                .id(pedido.getId())
                .clienteId(pedido.getCliente().getId())
                .clienteNombre(pedido.getCliente().getRazonSocial())
                .fechaRegistro(pedido.getFechaRegistro())
                .tipoCarga(pedido.getTipoCarga())
                .paisOrigen(pedido.getPaisOrigen())
                .paisDestino(pedido.getPaisDestino())
                .ciudadOrigen(pedido.getCiudadOrigen())
                .ciudadDestino(pedido.getCiudadDestino())
                .descripcionMercaderia(pedido.getDescripcionMercaderia())
                .numeroContenedorGuia(pedido.getNumeroContenedorGuia())
                .estado(pedido.getEstado())
                .codigoTracking(pedido.getCodigoTracking())
                .fechaEstimadaLlegada(pedido.getFechaEstimadaLlegada())
                .fechaLlegadaReal(pedido.getFechaLlegadaReal())
                .pesoTotalKg(pedido.getPesoTotalKg())
                .volumenTotalM3(pedido.getVolumenTotalM3())
                .valorDeclarado(pedido.getValorDeclarado())
                .moneda(pedido.getMoneda())
                .numeroBlAwb(pedido.getNumeroBlAwb())
                .puertoEmbarque(pedido.getPuertoEmbarque())
                .puertoDestino(pedido.getPuertoDestino())
                .empresaTransporte(pedido.getEmpresaTransporte())
                .requiereSeguro(pedido.getRequiereSeguro())
                .valorSeguro(pedido.getValorSeguro())
                .observaciones(pedido.getObservaciones())
                .subTotal(pedido.getSubTotal() != null ? pedido.getSubTotal().doubleValue() : null)
                .iva(pedido.getIva() != null ? pedido.getIva().doubleValue() : null)
                .total(pedido.getTotal() != null ? pedido.getTotal().doubleValue() : null)
                .direccionEntrega(pedido.getDireccionEntrega())
                .formaPago(pedido.getFormaPago())
                .createdAt(pedido.getCreatedAt())
                .updatedAt(pedido.getUpdatedAt())
                .build();
    }
}
