package com.logistic.control.controller;

import com.logistic.control.dto.request.FacturaRequest;
import com.logistic.control.dto.request.SifenDocumentoRequest;
import com.logistic.control.dto.response.*;
import com.logistic.control.entity.Cliente;
import com.logistic.control.entity.Factura;
import com.logistic.control.enums.EstadoFactura;
import com.logistic.control.repository.ClienteRepository;
import com.logistic.control.repository.FacturaRepository;
import com.logistic.control.service.SifenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
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
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Factura factura = toEntity(request, cliente);
        Factura saved = facturaRepository.save(factura);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaResponse> actualizarFactura(
            @PathVariable Long id,
            @Valid @RequestBody FacturaRequest request) {
        return facturaRepository.findById(id)
                .map(factura -> {
                    if (factura.getEstado() == EstadoFactura.APROBADA) {
                        throw new RuntimeException("No se puede modificar una factura aprobada");
                    }
                    Cliente cliente = clienteRepository.findById(request.getClienteId())
                            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
                    updateEntity(factura, request, cliente);
                    Factura updated = facturaRepository.save(factura);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

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

    private Factura toEntity(FacturaRequest request, Cliente cliente) {
        Factura factura = Factura.builder()
                .cliente(cliente)
                .subtotal(request.getSubtotal())
                .descuento(request.getDescuento() != null ? request.getDescuento() : 0.0)
                .moneda(request.getMoneda() != null ? request.getMoneda() : "PYG")
                .timbrado(request.getTimbrado())
                .establecimiento(request.getEstablecimiento())
                .puntoExpedicion(request.getPuntoExpedicion())
                .observaciones(request.getObservaciones())
                .build();

        factura.calcularTotales();
        return factura;
    }

    private void updateEntity(Factura factura, FacturaRequest request, Cliente cliente) {
        factura.setCliente(cliente);
        factura.setSubtotal(request.getSubtotal());
        factura.setDescuento(request.getDescuento() != null ? request.getDescuento() : 0.0);
        factura.setMoneda(request.getMoneda() != null ? request.getMoneda() : "PYG");
        factura.setTimbrado(request.getTimbrado());
        factura.setEstablecimiento(request.getEstablecimiento());
        factura.setPuntoExpedicion(request.getPuntoExpedicion());
        factura.setObservaciones(request.getObservaciones());
        factura.calcularTotales();
    }

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
}
