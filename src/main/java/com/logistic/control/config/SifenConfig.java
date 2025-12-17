package com.logistic.control.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para SIFEN (Sistema Integrado de Facturación Electrónica Nacional - Paraguay)
 */
@Configuration
@ConfigurationProperties(prefix = "sifen")
@Getter
@Setter
public class SifenConfig {

    /**
     * Ambiente de ejecución: "test" o "prod"
     */
    private String ambiente = "test";

    /**
     * URL del servicio web SIFEN para ambiente de test
     */
    private String urlTest = "https://sifen-test.set.gov.py/de/ws";

    /**
     * URL del servicio web SIFEN para ambiente de producción
     */
    private String urlProd = "https://sifen.set.gov.py/de/ws";

    /**
     * RUC del emisor
     */
    private String rucEmisor;

    /**
     * Razón social del emisor
     */
    private String razonSocialEmisor;

    /**
     * Nombre de fantasía del emisor
     */
    private String nombreFantasia;

    /**
     * Timbrado habilitado por SET
     */
    private String timbrado;

    /**
     * Código de establecimiento (3 dígitos)
     */
    private String establecimiento = "001";

    /**
     * Punto de expedición (3 dígitos)
     */
    private String puntoExpedicion = "001";

    /**
     * Ruta del certificado digital (.p12)
     */
    private String certificatePath;

    /**
     * Contraseña del certificado digital
     */
    private String certificatePassword;

    /**
     * Tipo de certificado: P12 o PEM
     */
    private String certificateType = "P12";

    /**
     * Timeout para conexión en segundos
     */
    private Integer connectTimeout = 10;

    /**
     * Timeout para lectura en segundos
     */
    private Integer readTimeout = 60;

    /**
     * Habilitar modo contingencia
     */
    private Boolean contingenciaEnabled = false;

    /**
     * Reintentos automáticos en caso de error
     */
    private Integer maxReintentos = 3;

    /**
     * Actividad económica principal
     */
    private String actividadEconomica;

    /**
     * Dirección del emisor
     */
    private String direccion;

    /**
     * Teléfono del emisor
     */
    private String telefono;

    /**
     * Email del emisor
     */
    private String email;

    /**
     * Ciudad del emisor
     */
    private String ciudad = "Asunción";

    /**
     * Código de departamento (2 dígitos)
     */
    private String departamento = "11"; // 11 = Capital

    /**
     * Obtiene la URL base según el ambiente configurado
     */
    public String getBaseUrl() {
        return "prod".equalsIgnoreCase(ambiente) ? urlProd : urlTest;
    }

    /**
     * Obtiene la URL completa para envío de documentos
     */
    public String getUrlEnvio() {
        return getBaseUrl() + "/sync/recibe.wsdl";
    }

    /**
     * Obtiene la URL completa para envío de lotes
     */
    public String getUrlEnvioLote() {
        return getBaseUrl() + "/async/recibe-lote.wsdl";
    }

    /**
     * Obtiene la URL completa para consultas
     */
    public String getUrlConsulta() {
        return getBaseUrl() + "/consultas/consulta.wsdl";
    }

    /**
     * Obtiene la URL completa para consulta de lotes
     */
    public String getUrlConsultaLote() {
        return getBaseUrl() + "/consultas/consulta-lote.wsdl";
    }

    /**
     * Obtiene la URL completa para consulta de RUC
     */
    public String getUrlConsultaRuc() {
        return getBaseUrl() + "/consultas/consulta-ruc.wsdl";
    }

    /**
     * Obtiene la URL completa para eventos
     */
    public String getUrlEvento() {
        return getBaseUrl() + "/eventos/evento.wsdl";
    }

    /**
     * Verifica si está en ambiente de producción
     */
    public boolean isProduccion() {
        return "prod".equalsIgnoreCase(ambiente);
    }

    /**
     * Verifica si está en ambiente de prueba
     */
    public boolean isTest() {
        return "test".equalsIgnoreCase(ambiente);
    }
}
