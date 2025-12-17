package com.logistic.control.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Respuesta de consulta de documento en SIFEN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SifenConsultaResponse {

    /**
     * CDC consultado
     */
    private String cdc;

    /**
     * Estado del documento en SIFEN
     * Aprobado, Rechazado, Cancelado, etc.
     */
    private String estado;

    /**
     * Código de estado
     */
    private String codigoEstado;

    /**
     * RUC del emisor
     */
    private String rucEmisor;

    /**
     * Razón social del emisor
     */
    private String razonSocialEmisor;

    /**
     * RUC del receptor
     */
    private String rucReceptor;

    /**
     * Razón social del receptor
     */
    private String razonSocialReceptor;

    /**
     * Fecha de emisión
     */
    private LocalDateTime fechaEmision;

    /**
     * Total del documento
     */
    private Double totalGeneral;

    /**
     * Mensaje adicional
     */
    private String mensaje;

    /**
     * XML de respuesta completo
     */
    private String xmlRespuesta;

    /**
     * Indica si la operación fue exitosa
     */
    private Boolean success;
}
