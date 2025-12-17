package com.logistic.control.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para generación de códigos QR
 */
@Slf4j
@Service
public class QRCodeService {

    private static final int DEFAULT_QR_SIZE = 250;
    private static final String DEFAULT_IMAGE_FORMAT = "PNG";

    /**
     * Genera un código QR a partir de un texto
     * @param text Texto a codificar
     * @param width Ancho del QR
     * @param height Alto del QR
     * @return Array de bytes de la imagen QR
     */
    public byte[] generateQRCode(String text, int width, int height) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, DEFAULT_IMAGE_FORMAT, outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Genera un código QR con tamaño por defecto
     */
    public byte[] generateQRCode(String text) throws WriterException, IOException {
        return generateQRCode(text, DEFAULT_QR_SIZE, DEFAULT_QR_SIZE);
    }

    /**
     * Genera un código QR y lo retorna como Base64
     */
    public String generateQRCodeBase64(String text) throws WriterException, IOException {
        byte[] qrCode = generateQRCode(text);
        return Base64.getEncoder().encodeToString(qrCode);
    }

    /**
     * Genera un código QR y lo retorna como Data URI para HTML
     */
    public String generateQRCodeDataUri(String text) throws WriterException, IOException {
        String base64 = generateQRCodeBase64(text);
        return "data:image/png;base64," + base64;
    }

    /**
     * Genera URL de verificación para un documento
     */
    public String generateVerificationUrl(String documentType, Long documentId, String verificationCode) {
        // URL base desde configuración o hardcoded
        String baseUrl = "https://logistica.com.py/verificar";
        return String.format("%s/%s/%d?code=%s", baseUrl, documentType, documentId, verificationCode);
    }

    /**
     * Genera código de verificación único para un documento
     */
    public String generateVerificationCode(String documentType, Long documentId) {
        String data = documentType + "-" + documentId + "-" + System.currentTimeMillis();
        return Base64.getUrlEncoder().encodeToString(data.getBytes());
    }
}
