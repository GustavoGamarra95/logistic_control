package com.logistic.control.controller;

import com.logistic.control.dto.request.ContainerRequest;
import com.logistic.control.dto.response.ContainerResponse;
import com.logistic.control.service.ContainerService;
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
 * Controller para gestión de Containers
 */
@RestController
@RequestMapping("/containers")
@RequiredArgsConstructor
@Tag(name = "Containers", description = "API para gestión de containers y consolidación de carga")
@SecurityRequirement(name = "bearerAuth")
public class ContainerController {

    private final ContainerService containerService;

    @Operation(summary = "Listar todos los containers")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping
    public ResponseEntity<Page<ContainerResponse>> listarContainers(Pageable pageable) {
        Page<ContainerResponse> containers = containerService.listarContainers(pageable);
        return ResponseEntity.ok(containers);
    }

    @Operation(summary = "Obtener container por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/{id}")
    public ResponseEntity<ContainerResponse> obtenerContainer(@PathVariable Long id) {
        ContainerResponse container = containerService.obtenerContainer(id);
        return ResponseEntity.ok(container);
    }

    @Operation(summary = "Buscar container por número")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @GetMapping("/numero/{numero}")
    public ResponseEntity<ContainerResponse> buscarPorNumero(@PathVariable String numero) {
        ContainerResponse container = containerService.buscarPorNumero(numero);
        return ResponseEntity.ok(container);
    }

    @Operation(summary = "Crear nuevo container")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PostMapping
    public ResponseEntity<ContainerResponse> crearContainer(@Valid @RequestBody ContainerRequest request) {
        ContainerResponse container = containerService.crearContainer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(container);
    }

    @Operation(summary = "Actualizar container")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ContainerResponse> actualizarContainer(
            @PathVariable Long id,
            @Valid @RequestBody ContainerRequest request) {
        ContainerResponse container = containerService.actualizarContainer(id, request);
        return ResponseEntity.ok(container);
    }

    @Operation(summary = "Consolidar productos en container")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PostMapping("/{id}/consolidar")
    public ResponseEntity<ContainerResponse> consolidarProductos(
            @PathVariable Long id,
            @RequestBody List<Long> productosIds) {
        ContainerResponse container = containerService.consolidarProductos(id, productosIds);
        return ResponseEntity.ok(container);
    }

    @Operation(summary = "Desconsolidar container")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'DEPOSITO')")
    @PostMapping("/{id}/desconsolidar")
    public ResponseEntity<ContainerResponse> desconsolidar(@PathVariable Long id) {
        ContainerResponse container = containerService.desconsolidar(id);
        return ResponseEntity.ok(container);
    }
}
