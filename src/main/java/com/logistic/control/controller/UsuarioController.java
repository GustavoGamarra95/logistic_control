package com.logistic.control.controller;

import com.logistic.control.dto.request.UsuarioRequest;
import com.logistic.control.dto.response.UsuarioResponse;
import com.logistic.control.enums.Role;
import com.logistic.control.service.UsuarioService;
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
 * Controller para gestión de Usuarios
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar todos los usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> listarUsuarios(Pageable pageable) {
        Page<UsuarioResponse> usuarios = usuarioService.listarUsuarios(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Obtener usuario por ID")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuario(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.obtenerUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Buscar usuario por username")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioResponse> buscarPorUsername(@PathVariable String username) {
        UsuarioResponse usuario = usuarioService.buscarPorUsername(username);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Buscar usuario por email")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> buscarPorEmail(@PathVariable String email) {
        UsuarioResponse usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Listar usuarios activos")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponse>> listarActivos() {
        List<UsuarioResponse> usuarios = usuarioService.listarActivos();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Crear nuevo usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @Operation(summary = "Actualizar usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.actualizarUsuario(id, request);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Desactivar usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioResponse> desactivar(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Activar usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<UsuarioResponse> activar(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.activarUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Bloquear usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/bloquear")
    public ResponseEntity<UsuarioResponse> bloquear(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.bloquearUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Desbloquear usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/desbloquear")
    public ResponseEntity<UsuarioResponse> desbloquear(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.desbloquearUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Agregar rol a usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/roles/agregar")
    public ResponseEntity<UsuarioResponse> agregarRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        UsuarioResponse usuario = usuarioService.agregarRole(id, role);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Remover rol de usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/roles/remover")
    public ResponseEntity<UsuarioResponse> removerRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        UsuarioResponse usuario = usuarioService.removerRole(id, role);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Eliminar usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
