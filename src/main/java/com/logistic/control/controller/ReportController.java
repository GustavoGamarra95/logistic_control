package com.logistic.control.controller;

import com.logistic.control.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller centralizado para generación de reportes PDF
 */
@Slf4j
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "API para generación de reportes PDF con QR")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final PedidoReportService pedidoReportService;
    private final FacturaReportService facturaReportService;
    private final ContenedorReportService contenedorReportService;
    private final InventarioReportService inventarioReportService;

    @Operation(summary = "Generar reporte PDF de pedido")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    @GetMapping("/pedidos/{id}")
    public ResponseEntity<byte[]> generarReportePedido(@PathVariable Long id) {
        try {
            byte[] pdfBytes = pedidoReportService.generatePedidoReport(id);
            return createPdfResponse(pdfBytes, "pedido_" + id + ".pdf");
        } catch (Exception e) {
            log.error("Error generando reporte de pedido: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Generar reporte PDF de factura")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'FINANZAS')")
    @GetMapping("/facturas/{id}")
    public ResponseEntity<byte[]> generarReporteFactura(@PathVariable Long id) {
        try {
            byte[] pdfBytes = facturaReportService.generateFacturaReport(id);
            return createPdfResponse(pdfBytes, "factura_" + id + ".pdf");
        } catch (Exception e) {
            log.error("Error generando reporte de factura: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Generar reporte PDF de contenedor")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/contenedores/{id}")
    public ResponseEntity<byte[]> generarReporteContenedor(@PathVariable Long id) {
        try {
            byte[] pdfBytes = contenedorReportService.generateContenedorReport(id);
            return createPdfResponse(pdfBytes, "contenedor_" + id + ".pdf");
        } catch (Exception e) {
            log.error("Error generando reporte de contenedor: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Generar reporte PDF de inventario")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/inventario")
    public ResponseEntity<byte[]> generarReporteInventario(
            @RequestParam(required = false) String categoria) {
        try {
            byte[] pdfBytes = inventarioReportService.generateInventarioReport(categoria);
            String filename = categoria != null ? "inventario_" + categoria + ".pdf" : "inventario_completo.pdf";
            return createPdfResponse(pdfBytes, filename);
        } catch (Exception e) {
            log.error("Error generando reporte de inventario", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<byte[]> createPdfResponse(byte[] pdfBytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", filename);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
