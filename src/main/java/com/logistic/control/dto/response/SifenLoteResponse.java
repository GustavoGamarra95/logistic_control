package com.logistic.control.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Respuesta de envío de lote a SIFEN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SifenLoteResponse {

    /**
     * Número de lote asignado por SIFEN
     */
    private String numeroLote;

    /**
     * Estado del lote
     */
    private String estado;

    /**
     * Fecha de recepción del lote
     */
    private LocalDateTime fechaRecepcion;

    /**
     * Cantidad de documentos en el lote
     */
    private Integer cantidadDocumentos;

    /**
     * Lista de resultados por documento
     */
    private List<DocumentoLoteResult> documentos;

    /**
     * Indica si la operación fue exitosa
     */
    private Boolean success;

    /**
     * Código de respuesta
     */
    private String codigo;

    /**
     * Mensaje
     */
    private String mensaje;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentoLoteResult {
        /**
         * CDC del documento
         */
        private String cdc;

        /**
         * Estado del documento
         */
        private String estado;

        /**
         * Código de estado
         */
        private String codigoEstado;

        /**
         * Mensaje de error (si aplica)
         */
        private String mensajeError;

        /**
         * Indica si fue aprobado
         */
        private Boolean aprobado;
    }
}
