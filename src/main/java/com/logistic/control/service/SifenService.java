package com.logistic.control.service;

import com.logistic.control.dto.request.SifenDocumentoRequest;
import com.logistic.control.dto.response.SifenConsultaResponse;
import com.logistic.control.dto.response.SifenLoteResponse;
import com.logistic.control.dto.response.SifenResponse;
import com.logistic.control.entity.Factura;
import com.logistic.control.enums.EstadoFactura;
import com.logistic.control.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio principal para gestión de facturación electrónica SIFEN
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SifenService {

    private final FacturaRepository facturaRepository;
    private final SifenXmlGenerator xmlGenerator;
    private final SifenXmlSigner xmlSigner;
    private final SifenClient sifenClient;
    private final QrCodeService qrCodeService;

    /**
     * Genera y envía una factura electrónica a SIFEN
     */
    @Transactional
    public SifenResponse enviarFacturaASifen(Long facturaId, SifenDocumentoRequest request) {
        try {
            log.info("Procesando envío de factura {} a SIFEN", facturaId);

            // Obtener la factura
            Factura factura = facturaRepository.findById(facturaId)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));

            // Validar estado
            if (factura.getEstado() == EstadoFactura.APROBADA) {
                throw new RuntimeException("La factura ya fue aprobada en SIFEN");
            }

            // Generar XML del documento electrónico
            String xmlSinFirmar = xmlGenerator.generarXmlFactura(factura);
            factura.setXmlDe(xmlSinFirmar);

            // Firmar el XML con XAdES-BES
            String xmlFirmado = xmlSigner.firmarXml(xmlSinFirmar);
            factura.setXmlDeFirmado(xmlFirmado);

            // Actualizar estado a "EN PROCESO"
            factura.setEstado(EstadoFactura.EN_PROCESO);
            factura.setFechaEnvioSifen(LocalDateTime.now());
            facturaRepository.save(factura);

            // Enviar a SIFEN
            SifenResponse response = sifenClient.enviarDocumento(xmlFirmado);

            // Procesar respuesta
            procesarRespuestaSifen(factura, response);

            log.info("Factura {} procesada con éxito. Estado: {}", facturaId, factura.getEstado());
            return response;

        } catch (Exception e) {
            log.error("Error enviando factura a SIFEN", e);
            throw new RuntimeException("Error enviando factura: " + e.getMessage(), e);
        }
    }

    /**
     * Envía múltiples facturas en lote a SIFEN
     */
    @Transactional
    public SifenLoteResponse enviarLoteASifen(List<Long> facturasIds) {
        try {
            log.info("Procesando envío de lote con {} facturas a SIFEN", facturasIds.size());

            List<String> xmlDocumentos = new ArrayList<>();
            List<Factura> facturas = new ArrayList<>();

            for (Long facturaId : facturasIds) {
                Factura factura = facturaRepository.findById(facturaId)
                        .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));

                if (factura.getEstado() == EstadoFactura.APROBADA) {
                    log.warn("Factura {} ya está aprobada, se omite del lote", facturaId);
                    continue;
                }

                // Generar y firmar XML
                String xmlSinFirmar = xmlGenerator.generarXmlFactura(factura);
                String xmlFirmado = xmlSigner.firmarXml(xmlSinFirmar);

                factura.setXmlDe(xmlSinFirmar);
                factura.setXmlDeFirmado(xmlFirmado);
                factura.setEstado(EstadoFactura.EN_PROCESO);
                factura.setFechaEnvioSifen(LocalDateTime.now());

                xmlDocumentos.add(xmlFirmado);
                facturas.add(factura);
            }

            if (xmlDocumentos.isEmpty()) {
                throw new RuntimeException("No hay facturas válidas para enviar en el lote");
            }

            // Guardar cambios antes de enviar
            facturaRepository.saveAll(facturas);

            // Enviar lote a SIFEN
            SifenLoteResponse response = sifenClient.enviarLote(xmlDocumentos);

            // Procesar respuesta del lote
            if (response.getSuccess()) {
                log.info("Lote enviado exitosamente. Número de lote: {}", response.getNumeroLote());
                
                // Actualizar facturas con el número de lote
                for (Factura factura : facturas) {
                    factura.setObservaciones(
                        (factura.getObservaciones() != null ? factura.getObservaciones() + " | " : "") +
                        "Lote: " + response.getNumeroLote()
                    );
                }
                facturaRepository.saveAll(facturas);
            }

            return response;

        } catch (Exception e) {
            log.error("Error enviando lote a SIFEN", e);
            throw new RuntimeException("Error enviando lote: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta el estado de una factura en SIFEN por CDC
     */
    public SifenConsultaResponse consultarEstadoFactura(String cdc) {
        try {
            log.info("Consultando estado de factura CDC: {}", cdc);
            return sifenClient.consultarDocumento(cdc);
        } catch (Exception e) {
            log.error("Error consultando estado de factura", e);
            throw new RuntimeException("Error consultando estado: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta el estado de un lote en SIFEN
     */
    public SifenLoteResponse consultarEstadoLote(String numeroLote) {
        try {
            log.info("Consultando estado de lote: {}", numeroLote);
            return sifenClient.consultarLote(numeroLote);
        } catch (Exception e) {
            log.error("Error consultando estado de lote", e);
            throw new RuntimeException("Error consultando lote: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza el estado de una factura basado en consulta a SIFEN
     */
    @Transactional
    public Factura actualizarEstadoDesdeConsulta(Long facturaId) {
        try {
            Factura factura = facturaRepository.findById(facturaId)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));

            if (factura.getCdc() == null || factura.getCdc().isEmpty()) {
                throw new RuntimeException("La factura no tiene CDC asignado");
            }

            SifenConsultaResponse consulta = sifenClient.consultarDocumento(factura.getCdc());

            if (consulta.getSuccess()) {
                if ("Aprobado".equalsIgnoreCase(consulta.getEstado())) {
                    factura.aprobarSifen(factura.getCdc(), consulta.getXmlRespuesta());
                    generarQrYKude(factura);
                } else if ("Rechazado".equalsIgnoreCase(consulta.getEstado())) {
                    factura.rechazarSifen(consulta.getCodigoEstado(), consulta.getMensaje());
                }

                facturaRepository.save(factura);
            }

            return factura;

        } catch (Exception e) {
            log.error("Error actualizando estado desde consulta", e);
            throw new RuntimeException("Error actualizando estado: " + e.getMessage(), e);
        }
    }

    /**
     * Procesa la respuesta de SIFEN y actualiza la factura
     */
    private void procesarRespuestaSifen(Factura factura, SifenResponse response) {
        factura.setRespuestaSifen(response.getXmlRespuesta());
        factura.setCodigoEstadoSifen(response.getCodigo());
        factura.setMensajeSifen(response.getMensaje());

        if (response.isAprobado()) {
            // Factura aprobada
            factura.aprobarSifen(response.getCdc(), response.getXmlRespuesta());
            
            // Generar QR y KuDE
            generarQrYKude(factura);
            
            log.info("Factura {} APROBADA en SIFEN. CDC: {}", factura.getId(), response.getCdc());
        } else {
            // Factura rechazada
            factura.rechazarSifen(response.getCodigo(), response.getMensaje());
            log.warn("Factura {} RECHAZADA en SIFEN. Código: {}, Mensaje: {}", 
                    factura.getId(), response.getCodigo(), response.getMensaje());
        }

        facturaRepository.save(factura);
    }

    /**
     * Genera el código QR y URL de KuDE para la factura
     */
    private void generarQrYKude(Factura factura) {
        try {
            // Generar URL de KuDE (consulta pública)
            String urlKude = String.format(
                "https://ekuatia.set.gov.py/consultas/qr?nVersion=150&Id=%s&dFeEmiDE=%s",
                factura.getCdc(),
                factura.getFechaEmision().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            factura.setUrlKude(urlKude);

            // Generar código QR
            String qrBase64 = qrCodeService.generarQrBase64(urlKude, 300, 300);
            factura.setQrCode(qrBase64);

            log.info("QR y KuDE generados para factura {}", factura.getId());

        } catch (Exception e) {
            log.error("Error generando QR y KuDE", e);
            // No lanzar excepción, solo registrar el error
        }
    }

    /**
     * Regenera el XML de una factura (útil para correcciones)
     */
    @Transactional
    public Factura regenerarXml(Long facturaId) {
        try {
            Factura factura = facturaRepository.findById(facturaId)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));

            if (factura.getEstado() == EstadoFactura.APROBADA) {
                throw new RuntimeException("No se puede regenerar XML de una factura aprobada");
            }

            String xmlSinFirmar = xmlGenerator.generarXmlFactura(factura);
            factura.setXmlDe(xmlSinFirmar);

            String xmlFirmado = xmlSigner.firmarXml(xmlSinFirmar);
            factura.setXmlDeFirmado(xmlFirmado);

            return facturaRepository.save(factura);

        } catch (Exception e) {
            log.error("Error regenerando XML", e);
            throw new RuntimeException("Error regenerando XML: " + e.getMessage(), e);
        }
    }

    /**
     * Valida la firma digital de una factura
     */
    public boolean validarFirmaDigital(Long facturaId) {
        try {
            Factura factura = facturaRepository.findById(facturaId)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));

            if (factura.getXmlDeFirmado() == null || factura.getXmlDeFirmado().isEmpty()) {
                log.warn("Factura {} no tiene XML firmado", facturaId);
                return false;
            }

            return xmlSigner.validarFirma(factura.getXmlDeFirmado());

        } catch (Exception e) {
            log.error("Error validando firma digital", e);
            return false;
        }
    }
}
