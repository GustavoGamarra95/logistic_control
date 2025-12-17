package com.logistic.control.service;

import com.logistic.control.config.SifenConfig;
import com.logistic.control.dto.response.SifenConsultaResponse;
import com.logistic.control.dto.response.SifenLoteResponse;
import com.logistic.control.dto.response.SifenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Cliente para comunicación con el servicio web SIFEN
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SifenClient {

    private final SifenConfig sifenConfig;
    private SSLSocketFactory sslSocketFactory;

    /**
     * Envía un documento electrónico a SIFEN
     */
    public SifenResponse enviarDocumento(String xmlDocumento) {
        try {
            log.info("Enviando documento a SIFEN...");
            
            configurarCertificado();
            
            String soapEnvelope = crearSoapEnvelope("rEnviDE", xmlDocumento);
            String responseXml = enviarRequest(sifenConfig.getUrlEnvio(), soapEnvelope, "recibe");
            
            return parsearRespuestaSifen(responseXml);
            
        } catch (Exception e) {
            log.error("Error enviando documento a SIFEN", e);
            return SifenResponse.builder()
                    .success(false)
                    .codigo("9999")
                    .mensaje("Error de comunicación: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Envía un lote de documentos a SIFEN
     */
    public SifenLoteResponse enviarLote(List<String> xmlDocumentos) {
        try {
            log.info("Enviando lote de {} documentos a SIFEN...", xmlDocumentos.size());
            
            configurarCertificado();
            
            // Crear ZIP con los documentos y codificar en base64
            byte[] zipBytes = crearZipLote(xmlDocumentos);
            String zipBase64 = Base64.getEncoder().encodeToString(zipBytes);
            
            String dId = UUID.randomUUID().toString().replace("-", "");
            String loteXml = String.format(
                "<rEnvioLote xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\">" +
                "  <dId>%s</dId>" +
                "  <xDE>%s</xDE>" +
                "</rEnvioLote>", 
                dId, zipBase64
            );
            
            String soapEnvelope = crearSoapEnvelope("rEnvioLote", loteXml);
            String responseXml = enviarRequest(sifenConfig.getUrlEnvioLote(), soapEnvelope, "recibe-lote");
            
            return parsearRespuestaLote(responseXml);
            
        } catch (Exception e) {
            log.error("Error enviando lote a SIFEN", e);
            return SifenLoteResponse.builder()
                    .success(false)
                    .codigo("9999")
                    .mensaje("Error de comunicación: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Consulta el estado de un documento por CDC
     */
    public SifenConsultaResponse consultarDocumento(String cdc) {
        try {
            log.info("Consultando documento CDC: {}", cdc);
            
            configurarCertificado();
            
            String dId = UUID.randomUUID().toString().replace("-", "");
            String consultaXml = String.format(
                "<rEnviConsDe>" +
                "  <dId>%s</dId>" +
                "  <dCDC>%s</dCDC>" +
                "</rEnviConsDe>",
                dId, cdc
            );
            
            String soapEnvelope = crearSoapEnvelope("rEnviConsDe", consultaXml);
            String responseXml = enviarRequest(sifenConfig.getUrlConsulta(), soapEnvelope, "consulta");
            
            return parsearRespuestaConsulta(responseXml);
            
        } catch (Exception e) {
            log.error("Error consultando documento en SIFEN", e);
            return SifenConsultaResponse.builder()
                    .success(false)
                    .cdc(cdc)
                    .mensaje("Error de comunicación: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Consulta el estado de un lote
     */
    public SifenLoteResponse consultarLote(String numeroLote) {
        try {
            log.info("Consultando lote: {}", numeroLote);
            
            configurarCertificado();
            
            String dId = UUID.randomUUID().toString().replace("-", "");
            String consultaXml = String.format(
                "<rEnviConsLoteDe>" +
                "  <dId>%s</dId>" +
                "  <dNumLote>%s</dNumLote>" +
                "</rEnviConsLoteDe>",
                dId, numeroLote
            );
            
            String soapEnvelope = crearSoapEnvelope("rEnviConsLoteDe", consultaXml);
            String responseXml = enviarRequest(sifenConfig.getUrlConsultaLote(), soapEnvelope, "consulta-lote");
            
            return parsearRespuestaLote(responseXml);
            
        } catch (Exception e) {
            log.error("Error consultando lote en SIFEN", e);
            return SifenLoteResponse.builder()
                    .success(false)
                    .codigo("9999")
                    .mensaje("Error de comunicación: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Configura el certificado digital para HTTPS mutual
     */
    private void configurarCertificado() throws Exception {
        if (sslSocketFactory != null) {
            return; // Ya configurado
        }

        log.debug("Configurando certificado: {}", sifenConfig.getCertificatePath());

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(sifenConfig.getCertificatePath())) {
            keyStore.load(fis, sifenConfig.getCertificatePassword().toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, sifenConfig.getCertificatePassword().toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        sslSocketFactory = sslContext.getSocketFactory();
    }

    /**
     * Crea el sobre SOAP para el request
     */
    private String crearSoapEnvelope(String operacion, String contenidoXml) {
        return String.format(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" +
            "  <env:Header/>" +
            "  <env:Body>" +
            "    %s" +
            "  </env:Body>" +
            "</env:Envelope>",
            contenidoXml
        );
    }

    /**
     * Envía el request HTTP al servicio SIFEN
     */
    private String enviarRequest(String urlEndpoint, String soapEnvelope, String soapAction) throws Exception {
        URL url = new URL(urlEndpoint);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        
        connection.setSSLSocketFactory(sslSocketFactory);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", soapAction);
        connection.setConnectTimeout(sifenConfig.getConnectTimeout() * 1000);
        connection.setReadTimeout(sifenConfig.getReadTimeout() * 1000);
        connection.setDoOutput(true);

        // Enviar request
        byte[] requestBytes = soapEnvelope.getBytes(StandardCharsets.UTF_8);
        connection.getOutputStream().write(requestBytes);

        // Leer respuesta
        int responseCode = connection.getResponseCode();
        log.debug("SIFEN Response Code: {}", responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            return new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            String errorResponse = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            log.error("SIFEN Error Response: {}", errorResponse);
            throw new RuntimeException("Error en respuesta SIFEN: HTTP " + responseCode);
        }
    }

    /**
     * Parsea la respuesta XML de SIFEN para un documento
     */
    private SifenResponse parsearRespuestaSifen(String xmlResponse) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            String codigo = obtenerValorXml(doc, "dCodRes");
            String mensaje = obtenerValorXml(doc, "dMsgRes");
            String cdc = obtenerValorXml(doc, "dCDC");
            String protocolo = obtenerValorXml(doc, "dProtAut");

            boolean success = "0100".equals(codigo) || "0200".equals(codigo);

            return SifenResponse.builder()
                    .success(success)
                    .codigo(codigo)
                    .mensaje(mensaje)
                    .cdc(cdc)
                    .protocoloAutorizacion(protocolo)
                    .fechaProcesamiento(LocalDateTime.now())
                    .xmlRespuesta(xmlResponse)
                    .build();

        } catch (Exception e) {
            log.error("Error parseando respuesta SIFEN", e);
            return SifenResponse.builder()
                    .success(false)
                    .codigo("9999")
                    .mensaje("Error parseando respuesta: " + e.getMessage())
                    .xmlRespuesta(xmlResponse)
                    .build();
        }
    }

    /**
     * Parsea la respuesta XML de consulta
     */
    private SifenConsultaResponse parsearRespuestaConsulta(String xmlResponse) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            return SifenConsultaResponse.builder()
                    .success(true)
                    .cdc(obtenerValorXml(doc, "dCDC"))
                    .estado(obtenerValorXml(doc, "dEstRes"))
                    .codigoEstado(obtenerValorXml(doc, "dCodRes"))
                    .rucEmisor(obtenerValorXml(doc, "dRucEm"))
                    .razonSocialEmisor(obtenerValorXml(doc, "dNomEmi"))
                    .rucReceptor(obtenerValorXml(doc, "dRucRec"))
                    .razonSocialReceptor(obtenerValorXml(doc, "dNomRec"))
                    .mensaje(obtenerValorXml(doc, "dMsgRes"))
                    .xmlRespuesta(xmlResponse)
                    .build();

        } catch (Exception e) {
            log.error("Error parseando respuesta de consulta SIFEN", e);
            return SifenConsultaResponse.builder()
                    .success(false)
                    .mensaje("Error parseando respuesta: " + e.getMessage())
                    .xmlRespuesta(xmlResponse)
                    .build();
        }
    }

    /**
     * Parsea la respuesta XML de lote
     */
    private SifenLoteResponse parsearRespuestaLote(String xmlResponse) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            String codigo = obtenerValorXml(doc, "dCodRes");
            boolean success = "0100".equals(codigo) || "0200".equals(codigo);

            return SifenLoteResponse.builder()
                    .success(success)
                    .codigo(codigo)
                    .mensaje(obtenerValorXml(doc, "dMsgRes"))
                    .numeroLote(obtenerValorXml(doc, "dNumLote"))
                    .estado(obtenerValorXml(doc, "dEstRes"))
                    .documentos(new ArrayList<>())
                    .build();

        } catch (Exception e) {
            log.error("Error parseando respuesta de lote SIFEN", e);
            return SifenLoteResponse.builder()
                    .success(false)
                    .codigo("9999")
                    .mensaje("Error parseando respuesta: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Obtiene un valor del documento XML
     */
    private String obtenerValorXml(Document doc, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Crea un archivo ZIP con los documentos XML
     */
    private byte[] crearZipLote(List<String> xmlDocumentos) throws Exception {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos);

        for (int i = 0; i < xmlDocumentos.size(); i++) {
            String filename = String.format("documento_%d.xml", i + 1);
            java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(filename);
            zos.putNextEntry(entry);
            zos.write(xmlDocumentos.get(i).getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        zos.close();
        return baos.toByteArray();
    }
}
