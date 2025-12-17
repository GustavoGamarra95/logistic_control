package com.logistic.control.dto.request;

import com.logistic.control.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de registro de nuevo usuario")
public class RegisterRequest {

    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario único", example = "juan.perez")
    private String username;

    @NotBlank(message = "Password es requerido")
    @Size(min = 6, message = "Password debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña (mínimo 8 caracteres recomendado)", example = "MiPassword123!")
    private String password;

    @NotBlank(message = "Nombre es requerido")
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre;

    @NotBlank(message = "Apellido es requerido")
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String apellido;

    @Email(message = "Email debe ser válido")
    @NotBlank(message = "Email es requerido")
    @Schema(description = "Email del usuario", example = "juan.perez@example.com")
    private String email;

    @Schema(description = "Teléfono del usuario (opcional)", example = "+595981234567")
    private String telefono;

    @Schema(description = "Roles asignados al usuario. Valores posibles: ADMIN, OPERADOR, CLIENTE, FINANZAS, DEPOSITO", 
            example = "[\"OPERADOR\"]", 
            allowableValues = {"ADMIN", "OPERADOR", "CLIENTE", "FINANZAS", "DEPOSITO"})
    private Set<Role> roles;
}
