package com.logistic.control.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Respuesta de SIFEN al enviar un documento electrónico
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SifenResponse {

    /**
     * Indica si la operación fue exitosa
     */
    private Boolean success;

    /**
     * Código de respuesta SIFEN
     * 0100 = Aprobado
     * 0200 = Aprobado con observaciones
     * 0300 = Rechazado
     */
    private String codigo;

    /**
     * Mensaje descriptivo de la respuesta
     */
    private String mensaje;

    /**
     * CDC (Código de Control del Documento) asignado por SIFEN
     * 44 dígitos
     */
    private String cdc;

    /**
     * Número de protocolo de autorización
     */
    private String protocoloAutorizacion;

    /**
     * Fecha y hora de procesamiento
     */
    private LocalDateTime fechaProcesamiento;

    /**
     * XML de respuesta completo
     */
    private String xmlRespuesta;

    /**
     * Indica si el documento fue aprobado
     */
    public boolean isAprobado() {
        return success != null && success && 
               (codigo != null && (codigo.equals("0100") || codigo.equals("0200")));
    }

    /**
     * Indica si el documento fue rechazado
     */
    public boolean isRechazado() {
        return !isAprobado();
    }
}
