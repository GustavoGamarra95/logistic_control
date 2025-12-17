package com.logistic.control.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de eliminación lógica con motivo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteRequest {

    @Size(max = 500, message = "El motivo no puede exceder los 500 caracteres")
    private String reason;
}
