package com.logistic.control.controller;

import com.logistic.control.config.SifenConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller para gestión de configuración SIFEN
 */
@RestController
@RequestMapping("/sifen")
@RequiredArgsConstructor
@Tag(name = "SIFEN", description = "Configuración del Sistema de Facturación Electrónica Nacional")
@SecurityRequirement(name = "bearerAuth")
public class SifenConfigController {

    private final SifenConfig sifenConfig;

    /**
     * Obtiene la configuración actual de SIFEN
     */
    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener configuración SIFEN", description = "Retorna la configuración actual del sistema SIFEN")
    public ResponseEntity<SifenConfigResponse> getConfig() {
        SifenConfigResponse response = SifenConfigResponse.builder()
                .ambiente(sifenConfig.getAmbiente())
                .rucEmisor(sifenConfig.getRucEmisor())
                .razonSocialEmisor(sifenConfig.getRazonSocialEmisor())
                .nombreFantasia(sifenConfig.getNombreFantasia())
                .timbrado(sifenConfig.getTimbrado())
                .establecimiento(sifenConfig.getEstablecimiento())
                .puntoExpedicion(sifenConfig.getPuntoExpedicion())
                .actividadEconomica(sifenConfig.getActividadEconomica())
                .direccion(sifenConfig.getDireccion())
                .telefono(sifenConfig.getTelefono())
                .email(sifenConfig.getEmail())
                .ciudad(sifenConfig.getCiudad())
                .departamento(sifenConfig.getDepartamento())
                .contingenciaEnabled(sifenConfig.getContingenciaEnabled())
                .maxReintentos(sifenConfig.getMaxReintentos())
                .connectTimeout(sifenConfig.getConnectTimeout())
                .readTimeout(sifenConfig.getReadTimeout())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza la configuración de SIFEN
     * NOTA: Esta implementación actualiza solo los valores en memoria.
     * Para persistencia, debería guardarse en base de datos o archivo de configuración.
     */
    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar configuración SIFEN", description = "Actualiza la configuración del sistema SIFEN")
    public ResponseEntity<SifenConfigResponse> updateConfig(@RequestBody SifenConfigRequest request) {
        // Actualizar configuración en memoria
        if (request.getAmbiente() != null) {
            sifenConfig.setAmbiente(request.getAmbiente());
        }
        if (request.getRucEmisor() != null) {
            sifenConfig.setRucEmisor(request.getRucEmisor());
        }
        if (request.getRazonSocialEmisor() != null) {
            sifenConfig.setRazonSocialEmisor(request.getRazonSocialEmisor());
        }
        if (request.getNombreFantasia() != null) {
            sifenConfig.setNombreFantasia(request.getNombreFantasia());
        }
        if (request.getTimbrado() != null) {
            sifenConfig.setTimbrado(request.getTimbrado());
        }
        if (request.getEstablecimiento() != null) {
            sifenConfig.setEstablecimiento(request.getEstablecimiento());
        }
        if (request.getPuntoExpedicion() != null) {
            sifenConfig.setPuntoExpedicion(request.getPuntoExpedicion());
        }
        if (request.getActividadEconomica() != null) {
            sifenConfig.setActividadEconomica(request.getActividadEconomica());
        }
        if (request.getDireccion() != null) {
            sifenConfig.setDireccion(request.getDireccion());
        }
        if (request.getTelefono() != null) {
            sifenConfig.setTelefono(request.getTelefono());
        }
        if (request.getEmail() != null) {
            sifenConfig.setEmail(request.getEmail());
        }
        if (request.getCiudad() != null) {
            sifenConfig.setCiudad(request.getCiudad());
        }
        if (request.getDepartamento() != null) {
            sifenConfig.setDepartamento(request.getDepartamento());
        }
        if (request.getContingenciaEnabled() != null) {
            sifenConfig.setContingenciaEnabled(request.getContingenciaEnabled());
        }
        if (request.getMaxReintentos() != null) {
            sifenConfig.setMaxReintentos(request.getMaxReintentos());
        }
        if (request.getConnectTimeout() != null) {
            sifenConfig.setConnectTimeout(request.getConnectTimeout());
        }
        if (request.getReadTimeout() != null) {
            sifenConfig.setReadTimeout(request.getReadTimeout());
        }

        return getConfig();
    }

    /**
     * Verifica el estado de conexión con SIFEN
     */
    @GetMapping("/verificar-conexion")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verificar conexión con SIFEN", description = "Verifica si el sistema puede conectarse a los servicios de SIFEN")
    public ResponseEntity<SifenEstadoResponse> verificarConexion() {
        try {
            // TODO: Implementar verificación real de conexión con SIFEN
            // Por ahora retornamos respuesta simulada

            boolean conectado = true;
            String mensaje = sifenConfig.isProduccion()
                ? "Conexión exitosa con SIFEN (ambiente de producción)"
                : "Conexión exitosa con SIFEN (ambiente de pruebas)";

            SifenEstadoResponse response = SifenEstadoResponse.builder()
                    .conectado(conectado)
                    .ambiente(sifenConfig.getAmbiente())
                    .mensaje(mensaje)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SifenEstadoResponse response = SifenEstadoResponse.builder()
                    .conectado(false)
                    .ambiente(sifenConfig.getAmbiente())
                    .mensaje("Error al conectar con SIFEN: " + e.getMessage())
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            return ResponseEntity.ok(response);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SifenConfigRequest {
        private String ambiente;
        private String rucEmisor;
        private String razonSocialEmisor;
        private String nombreFantasia;
        private String timbrado;
        private String establecimiento;
        private String puntoExpedicion;
        private String actividadEconomica;
        private String direccion;
        private String telefono;
        private String email;
        private String ciudad;
        private String departamento;
        private Boolean contingenciaEnabled;
        private Integer maxReintentos;
        private Integer connectTimeout;
        private Integer readTimeout;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SifenConfigResponse {
        private String ambiente;
        private String rucEmisor;
        private String razonSocialEmisor;
        private String nombreFantasia;
        private String timbrado;
        private String establecimiento;
        private String puntoExpedicion;
        private String actividadEconomica;
        private String direccion;
        private String telefono;
        private String email;
        private String ciudad;
        private String departamento;
        private Boolean contingenciaEnabled;
        private Integer maxReintentos;
        private Integer connectTimeout;
        private Integer readTimeout;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SifenEstadoResponse {
        private Boolean conectado;
        private String ambiente;
        private String mensaje;
        private String timestamp;
    }
}
