package com.logistic.control.controller;

import com.logistic.control.dto.request.DevolucionRequest;
import com.logistic.control.dto.response.DevolucionResponse;
import com.logistic.control.service.DevolucionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para gestión de devoluciones de ventas.
 */
@RestController
@RequestMapping("/devoluciones")
@RequiredArgsConstructor
public class DevolucionController {

    private final DevolucionService devolucionService;

    /**
     * Listar todas las devoluciones con paginación.
     */
    @GetMapping
    public ResponseEntity<Page<DevolucionResponse>> listarDevoluciones(Pageable pageable) {
        Page<DevolucionResponse> devoluciones = devolucionService.listarDevoluciones(pageable);
        return ResponseEntity.ok(devoluciones);
    }

    /**
     * Obtener devolución por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DevolucionResponse> obtenerDevolucion(@PathVariable Long id) {
        DevolucionResponse devolucion = devolucionService.obtenerDevolucion(id);
        return ResponseEntity.ok(devolucion);
    }

    /**
     * Listar devoluciones pendientes de aprobación.
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<DevolucionResponse>> listarDevolucionesPendientes() {
        List<DevolucionResponse> devoluciones = devolucionService.listarDevolucionesPendientes();
        return ResponseEntity.ok(devoluciones);
    }

    /**
     * Buscar devoluciones por cliente.
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<DevolucionResponse>> buscarPorCliente(
            @PathVariable Long clienteId,
            Pageable pageable) {
        Page<DevolucionResponse> devoluciones = devolucionService.buscarPorCliente(clienteId, pageable);
        return ResponseEntity.ok(devoluciones);
    }

    /**
     * Crear una nueva devolución.
     */
    @PostMapping
    public ResponseEntity<DevolucionResponse> crearDevolucion(
            @Valid @RequestBody DevolucionRequest request) {
        DevolucionResponse devolucion = devolucionService.crearDevolucion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(devolucion);
    }

    /**
     * Aprobar una devolución y procesarla automáticamente.
     * Ejecuta el flujo completo:
     * - Aprobar devolución
     * - Procesar según tipo (producto físico/corrección/ajuste)
     * - Generar nota de crédito si corresponde
     * - Completar devolución
     */
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<DevolucionResponse> aprobarDevolucion(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {
        DevolucionResponse devolucion = devolucionService.aprobarDevolucion(id, usuarioId);
        return ResponseEntity.ok(devolucion);
    }

    /**
     * Rechazar una devolución.
     */
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<DevolucionResponse> rechazarDevolucion(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @RequestBody Map<String, String> payload) {
        String motivo = payload.get("motivo");
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de rechazo es requerido");
        }
        DevolucionResponse devolucion = devolucionService.rechazarDevolucion(id, usuarioId, motivo);
        return ResponseEntity.ok(devolucion);
    }
}
