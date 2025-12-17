package com.logistic.control.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para generación de códigos QR
 */
@Service
@Slf4j
public class QrCodeService {

    /**
     * Genera un código QR y lo retorna en formato Base64

     */
    public String generarQrBase64(String contenido, int ancho, int alto) {
        try {
            log.debug("Generando código QR para contenido de longitud: {}", contenido.length());

            // Configurar hints para el QR
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            // Generar el código QR
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, ancho, alto, hints);

            // Convertir a imagen
            BufferedImage image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            // Convertir a Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            log.debug("Código QR generado exitosamente. Tamaño: {} bytes", imageBytes.length);
            return "data:image/png;base64," + base64;

        } catch (Exception e) {
            log.error("Error generando código QR", e);
            throw new RuntimeException("Error generando código QR: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un código QR con tamaño predeterminado

     */
    public String generarQrBase64(String contenido) {
        return generarQrBase64(contenido, 300, 300);
    }

    /**
     * Genera bytes de la imagen QR (útil para guardar en archivos)

     */
    public byte[] generarQrBytes(String contenido, int ancho, int alto) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, ancho, alto, hints);

            BufferedImage image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generando bytes de QR", e);
            throw new RuntimeException("Error generando QR: " + e.getMessage(), e);
        }
    }
}
