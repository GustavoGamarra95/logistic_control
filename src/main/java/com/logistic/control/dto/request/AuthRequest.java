package com.logistic.control.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de autenticación")
public class AuthRequest {

    @NotBlank(message = "Username es requerido")
    @Schema(
        description = "Nombre de usuario. Usuarios de prueba: admin, operador, cliente1, finanzas, deposito", 
        example = "admin",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotBlank(message = "Password es requerido")
    @Schema(
        description = "Contraseña. Para usuarios de prueba usar: demo123", 
        example = "demo123",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}
