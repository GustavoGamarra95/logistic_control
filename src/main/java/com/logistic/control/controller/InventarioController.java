package com.logistic.control.controller;

import com.logistic.control.dto.request.InventarioRequest;
import com.logistic.control.dto.response.InventarioResponse;
import com.logistic.control.enums.EstadoInventario;
import com.logistic.control.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Inventario
 */
@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "API para gestión de inventario y almacenamiento")
@SecurityRequirement(name = "bearerAuth")
public class InventarioController {

    private final InventarioService inventarioService;

    @Operation(summary = "Listar todo el inventario")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping
    public ResponseEntity<Page<InventarioResponse>> listarInventario(Pageable pageable) {
        Page<InventarioResponse> inventario = inventarioService.listarInventario(pageable);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Obtener inventario por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponse> obtenerInventario(@PathVariable Long id) {
        InventarioResponse inventario = inventarioService.obtenerInventario(id);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Listar inventario por cliente")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO', 'CLIENTE')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<InventarioResponse>> listarPorCliente(@PathVariable Long clienteId) {
        List<InventarioResponse> inventario = inventarioService.buscarPorCliente(clienteId);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Listar inventario por producto")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<InventarioResponse>> listarPorProducto(@PathVariable Long productoId) {
        List<InventarioResponse> inventario = inventarioService.buscarPorProducto(productoId);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Listar inventario por estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<InventarioResponse>> listarPorEstado(@PathVariable EstadoInventario estado) {
        List<InventarioResponse> inventario = inventarioService.buscarPorEstado(estado);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Listar inventario disponible")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/disponible")
    public ResponseEntity<List<InventarioResponse>> listarDisponible() {
        List<InventarioResponse> inventario = inventarioService.buscarPorEstado(EstadoInventario.DISPONIBLE);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Buscar inventario por ubicación")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/ubicacion/{ubicacion}")
    public ResponseEntity<List<InventarioResponse>> buscarPorUbicacion(@PathVariable String ubicacion) {
        List<InventarioResponse> inventario = inventarioService.buscarPorUbicacion(ubicacion);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Crear nuevo registro de inventario")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @PostMapping
    public ResponseEntity<InventarioResponse> crearInventario(@Valid @RequestBody InventarioRequest request) {
        InventarioResponse inventario = inventarioService.crearInventario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventario);
    }

    @Operation(summary = "Actualizar inventario")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponse> actualizarInventario(
            @PathVariable Long id,
            @Valid @RequestBody InventarioRequest request) {
        InventarioResponse inventario = inventarioService.actualizarInventario(id, request);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Registrar entrada de mercadería")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @PatchMapping("/{id}/entrada")
    public ResponseEntity<InventarioResponse> registrarEntrada(@PathVariable Long id) {
        InventarioResponse inventario = inventarioService.registrarEntrada(id);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Registrar salida de mercadería")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @PatchMapping("/{id}/salida")
    public ResponseEntity<InventarioResponse> registrarSalida(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        InventarioResponse inventario = inventarioService.registrarSalida(id, cantidad);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Reservar inventario")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @PatchMapping("/{id}/reservar")
    public ResponseEntity<InventarioResponse> reservar(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        InventarioResponse inventario = inventarioService.reservar(id, cantidad);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Liberar reserva de inventario")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @PatchMapping("/{id}/liberar-reserva")
    public ResponseEntity<InventarioResponse> liberarReserva(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        InventarioResponse inventario = inventarioService.liberarReserva(id, cantidad);
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Eliminar inventario")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable Long id) {
        inventarioService.eliminarInventario(id);
        return ResponseEntity.noContent().build();
    }
}
