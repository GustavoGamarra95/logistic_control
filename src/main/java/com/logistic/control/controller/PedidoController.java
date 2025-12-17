package com.logistic.control.controller;

import com.logistic.control.dto.DeleteRequest;
import com.logistic.control.dto.request.PedidoRequest;
import com.logistic.control.dto.response.PedidoResponse;
import com.logistic.control.enums.EstadoPedido;
import com.logistic.control.service.PedidoReportService;
import com.logistic.control.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller para gestión de Pedidos/Envíos
 */
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "API para gestión de pedidos y envíos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoReportService pedidoReportService;

    @Operation(summary = "Listar todos los pedidos", description = "Obtiene una lista paginada de todos los pedidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping
    public ResponseEntity<Page<PedidoResponse>> listarPedidos(Pageable pageable) {
        Page<PedidoResponse> pedidos = pedidoService.listarPedidos(pageable);
        return ResponseEntity.ok(pedidos);
    }

    @Operation(summary = "Obtener pedido por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPedido(@PathVariable Long id) {
        PedidoResponse pedido = pedidoService.obtenerPedido(id);
        return ResponseEntity.ok(pedido);
    }

    @Operation(summary = "Buscar pedido por código de tracking")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    @GetMapping("/tracking/{codigo}")
    public ResponseEntity<PedidoResponse> buscarPorTracking(@PathVariable String codigo) {
        PedidoResponse pedido = pedidoService.buscarPorTracking(codigo);
        return ResponseEntity.ok(pedido);
    }

    @Operation(summary = "Listar pedidos de un cliente")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<PedidoResponse>> listarPorCliente(
            @PathVariable Long clienteId,
            Pageable pageable) {
        Page<PedidoResponse> pedidos = pedidoService.buscarPorCliente(clienteId, pageable);
        return ResponseEntity.ok(pedidos);
    }

    @Operation(summary = "Listar pedidos por estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<Page<PedidoResponse>> listarPorEstado(
            @PathVariable EstadoPedido estado,
            Pageable pageable) {
        Page<PedidoResponse> pedidos = pedidoService.listarPorEstado(estado, pageable);
        return ResponseEntity.ok(pedidos);
    }

    @Operation(summary = "Listar pedidos por rango de fechas estimadas")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @GetMapping("/fecha-estimada")
    public ResponseEntity<Page<PedidoResponse>> listarPorFechaEstimada(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta,
            Pageable pageable) {
        Page<PedidoResponse> pedidos = pedidoService.buscarPorFechaEstimada(desde, hasta, pageable);
        return ResponseEntity.ok(pedidos);
    }

    @Operation(summary = "Crear nuevo pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse pedido = pedidoService.crearPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @Operation(summary = "Actualizar pedido")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoRequest request) {
        PedidoResponse pedido = pedidoService.actualizarPedido(id, request);
        return ResponseEntity.ok(pedido);
    }

    @Operation(summary = "Cambiar estado del pedido")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado,
            @RequestParam(required = false) String comentario) {
        PedidoResponse pedido = pedidoService.cambiarEstado(id, estado, comentario);
        return ResponseEntity.ok(pedido);
    }

    @Operation(summary = "Eliminar pedido (soft delete)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) DeleteRequest request) {
        String reason = request != null ? request.getReason() : null;
        pedidoService.eliminarPedido(id, reason);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Calcular costo del pedido")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'FINANZAS')")
    @GetMapping("/{id}/costo")
    public ResponseEntity<PedidoResponse> calcularCosto(@PathVariable Long id) {
        PedidoResponse pedido = pedidoService.calcularCosto(id);
        return ResponseEntity.ok(pedido);
    }

    @Operation(summary = "Generar reporte PDF del pedido")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generarPDF(@PathVariable Long id) {
        try {
            byte[] pdfBytes = pedidoReportService.generatePedidoReport(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "pedido_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
