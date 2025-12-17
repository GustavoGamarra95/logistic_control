package com.logistic.control.controller;

import com.logistic.control.dto.request.ProveedorRequest;
import com.logistic.control.dto.response.ProveedorResponse;
import com.logistic.control.enums.TipoProveedor;
import com.logistic.control.service.ProveedorService;
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
 * Controller para gestión de Proveedores
 */
@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "API para gestión de proveedores y servicios")
@SecurityRequirement(name = "bearerAuth")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @Operation(summary = "Listar todos los proveedores")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'FINANZAS')")
    @GetMapping
    public ResponseEntity<Page<ProveedorResponse>> listarProveedores(Pageable pageable) {
        Page<ProveedorResponse> proveedores = proveedorService.listarProveedores(pageable);
        return ResponseEntity.ok(proveedores);
    }

    @Operation(summary = "Obtener proveedor por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'FINANZAS')")
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponse> obtenerProveedor(@PathVariable Long id) {
        ProveedorResponse proveedor = proveedorService.obtenerProveedor(id);
        return ResponseEntity.ok(proveedor);
    }

    @Operation(summary = "Listar proveedores por tipo")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'FINANZAS')")
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ProveedorResponse>> listarPorTipo(@PathVariable TipoProveedor tipo) {
        List<ProveedorResponse> proveedores = proveedorService.listarPorTipo(tipo);
        return ResponseEntity.ok(proveedores);
    }

    @Operation(summary = "Buscar proveedor por RUC")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'FINANZAS')")
    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<ProveedorResponse> buscarPorRuc(@PathVariable String ruc) {
        ProveedorResponse proveedor = proveedorService.buscarPorRuc(ruc);
        return ResponseEntity.ok(proveedor);
    }

    @Operation(summary = "Crear nuevo proveedor")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PostMapping
    public ResponseEntity<ProveedorResponse> crearProveedor(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse proveedor = proveedorService.crearProveedor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedor);
    }

    @Operation(summary = "Actualizar proveedor")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponse> actualizarProveedor(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse proveedor = proveedorService.actualizarProveedor(id, request);
        return ResponseEntity.ok(proveedor);
    }

    @Operation(summary = "Eliminar proveedor")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
