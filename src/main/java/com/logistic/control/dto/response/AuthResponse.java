package com.logistic.control.dto.response;

import com.logistic.control.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con tokens JWT")
public class AuthResponse {

    @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "Token de actualización JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    
    @Builder.Default
    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "Nombre de usuario", example = "juan.perez")
    private String username;
    
    @Schema(description = "Email del usuario", example = "juan.perez@example.com")
    private String email;
    
    @Schema(description = "Roles del usuario", example = "[\"OPERADOR\", \"CLIENTE\"]")
    private Set<Role> roles;
}
