package com.logistic.control.controller;

import com.logistic.control.dto.request.ClienteRequest;
import com.logistic.control.dto.response.ClienteResponse;
import com.logistic.control.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * Controller para gestión de Clientes
 */
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API para gestión de clientes/empresas")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(summary = "Listar todos los clientes", description = "Obtiene una lista paginada de todos los clientes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'FINANZAS')")
    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listarClientes(
            @org.springframework.data.web.PageableDefault(
                page = 0,
                size = 20,
                sort = "id",
                direction = org.springframework.data.domain.Sort.Direction.DESC
            ) Pageable pageable) {
        Page<ClienteResponse> clientes = clienteService.listarClientes(pageable);
        return ResponseEntity.ok(clientes);
    }

    @Operation(summary = "Obtener cliente por ID", description = "Obtiene los detalles de un cliente específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'FINANZAS', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerCliente(@PathVariable Long id) {
        ClienteResponse cliente = clienteService.obtenerCliente(id);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Buscar cliente por RUC", description = "Busca un cliente por su número de RUC")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'FINANZAS')")
    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<ClienteResponse> buscarPorRuc(@PathVariable String ruc) {
        ClienteResponse cliente = clienteService.buscarPorRuc(ruc);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Buscar cliente por email", description = "Busca un cliente por su dirección de email")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResponse> buscarPorEmail(@PathVariable String email) {
        ClienteResponse cliente = clienteService.buscarPorEmail(email);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Crear nuevo cliente", description = "Crea un nuevo cliente en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Cliente ya existe")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse cliente = clienteService.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        ClienteResponse cliente = clienteService.actualizarCliente(id, request);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar crédito", description = "Actualiza el crédito disponible de un cliente")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANZAS')")
    @PatchMapping("/{id}/credito")
    public ResponseEntity<ClienteResponse> actualizarCredito(
            @PathVariable Long id,
            @RequestParam Double monto) {
        ClienteResponse cliente = clienteService.actualizarCredito(id, monto);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Verificar RUC en SIFEN", description = "Verifica el estado de un RUC en SIFEN")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @PostMapping("/{id}/verificar-ruc")
    public ResponseEntity<ClienteResponse> verificarRuc(@PathVariable Long id) {
        ClienteResponse cliente = clienteService.verificarRucEnSifen(id);
        return ResponseEntity.ok(cliente);
    }
}
