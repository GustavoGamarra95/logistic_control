package com.logistic.control.controller;

import com.logistic.control.dto.request.ProductoRequest;
import com.logistic.control.dto.response.ProductoResponse;
import com.logistic.control.service.ProductoService;
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

/**
 * Controller para gestión de Productos
 */
@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para gestión de productos y mercaderías")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Listar todos los productos")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping
    public ResponseEntity<Page<ProductoResponse>> listarProductos(Pageable pageable) {
        Page<ProductoResponse> productos = productoService.listarProductos(pageable);
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener producto por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerProducto(@PathVariable Long id) {
        ProductoResponse producto = productoService.obtenerProducto(id);
        return ResponseEntity.ok(producto);
    }

    @Operation(summary = "Buscar producto por código")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProductoResponse> buscarPorCodigo(@PathVariable String codigo) {
        ProductoResponse producto = productoService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(producto);
    }

    @Operation(summary = "Buscar productos por código NCM")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @GetMapping("/ncm/{ncm}")
    public ResponseEntity<Page<ProductoResponse>> buscarPorNcm(
            @PathVariable String ncm,
            Pageable pageable) {
        Page<ProductoResponse> productos = productoService.buscarPorCodigoNcm(ncm, pageable);
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Listar productos peligrosos")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/peligrosos")
    public ResponseEntity<Page<ProductoResponse>> listarPeligrosos(Pageable pageable) {
        Page<ProductoResponse> productos = productoService.listarProductosPeligrosos(pageable);
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Listar productos que requieren refrigeración")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/refrigeracion")
    public ResponseEntity<Page<ProductoResponse>> listarRequierenRefrigeracion(Pageable pageable) {
        Page<ProductoResponse> productos = productoService.listarProductosRefrigerados(pageable);
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Crear nuevo producto")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PostMapping
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = productoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @Operation(summary = "Actualizar producto")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = productoService.actualizarProducto(id, request);
        return ResponseEntity.ok(producto);
    }

    @Operation(summary = "Eliminar producto")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar productos por nombre")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/search")
    public ResponseEntity<Page<ProductoResponse>> buscarPorNombre(
            @RequestParam String nombre,
            Pageable pageable) {
        Page<ProductoResponse> productos = productoService.buscarPorNombre(nombre, pageable);
        return ResponseEntity.ok(productos);
    }
}
