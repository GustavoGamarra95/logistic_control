package com.logistic.control.service;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio base para generación de reportes PDF con JasperReports
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final QRCodeService qrCodeService;

    @Value("${jasper.reports-path:classpath:templates/reports/}")
    private String reportsPath;

    @Value("${jasper.images-path:classpath:static/images/}")
    private String imagesPath;

    /**
     * Genera un reporte PDF
     * @param reportName Nombre del archivo jrxml (sin extensión)
     * @param parameters Parámetros del reporte
     * @param dataSource Fuente de datos (lista de objetos)
     * @return Array de bytes del PDF generado
     */
    public byte[] generatePdfReport(String reportName, Map<String, Object> parameters, List<?> dataSource)
            throws JRException, IOException {

        log.info("Generando reporte: {}", reportName);

        // Cargar template
        String templatePath = String.format("templates/reports/%s.jrxml", reportName);
        InputStream templateStream = new ClassPathResource(templatePath).getInputStream();

        // Compilar template
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

        // Crear data source
        JRBeanCollectionDataSource jrDataSource = new JRBeanCollectionDataSource(dataSource);

        // Llenar reporte
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrDataSource);

        // Exportar a PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    /**
     * Genera un reporte con QR code de verificación
     */
    public byte[] generatePdfReportWithQR(String reportName, Map<String, Object> parameters, List<?> dataSource,
                                          String documentType, Long documentId)
            throws JRException, IOException, WriterException {

        // Generar código de verificación
        String verificationCode = qrCodeService.generateVerificationCode(documentType, documentId);
        String verificationUrl = qrCodeService.generateVerificationUrl(documentType, documentId, verificationCode);

        // Generar QR code
        byte[] qrCodeBytes = qrCodeService.generateQRCode(verificationUrl, 150, 150);

        // Agregar QR code a parámetros
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put("QR_CODE", new ByteArrayInputStream(qrCodeBytes));
        parameters.put("VERIFICATION_CODE", verificationCode);
        parameters.put("VERIFICATION_URL", verificationUrl);

        return generatePdfReport(reportName, parameters, dataSource);
    }

    /**
     * Carga un logo o imagen desde resources
     */
    public InputStream loadImage(String imageName) throws IOException {
        String imagePath = String.format("static/images/%s", imageName);
        return new ClassPathResource(imagePath).getInputStream();
    }

    /**
     * Crea parámetros básicos comunes para todos los reportes
     */
    public Map<String, Object> createBaseParameters() throws IOException {
        Map<String, Object> parameters = new HashMap<>();

        try {
            parameters.put("LOGO", loadImage("logo.png"));
        } catch (IOException e) {
            log.warn("Logo no encontrado, continuando sin logo");
        }

        parameters.put("GENERATED_DATE", new java.util.Date());

        return parameters;
    }
}
