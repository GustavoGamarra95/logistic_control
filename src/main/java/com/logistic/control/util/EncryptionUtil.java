package com.logistic.control.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad para cifrado/descifrado AES-GCM de datos sensibles
 */
@Component
public class EncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    @Value("${encryption.secret-key}")
    private String secretKeyString;

    /**
     * Cifra un texto usando AES-GCM
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            SecretKey secretKey = getSecretKey();
            byte[] iv = generateIV();

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Combinar IV + datos cifrados
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (java.security.GeneralSecurityException | IllegalArgumentException e) {
            throw new RuntimeException("Error al cifrar datos", e);
        }
    }

    /**
     * Descifra un texto usando AES-GCM
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        try {
            SecretKey secretKey = getSecretKey();
            byte[] decodedData = Base64.getDecoder().decode(encryptedText);

            // Extraer IV y datos cifrados
            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedData);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] encryptedData = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedData);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (java.security.GeneralSecurityException | IllegalArgumentException e) {
            throw new RuntimeException("Error al descifrar datos", e);
        }
    }

    /**
     * Intenta descifrar un texto. Si falla (datos legacy no cifrados), retorna el texto original.
     * Esto permite compatibilidad con datos existentes que fueron insertados antes del cifrado.
     */
    public String decryptOrReturnPlainText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        try {
            // Intentar descifrar
            return decrypt(text);
        } catch (RuntimeException e) {
            // Si falla el descifrado, asumir que es texto plano (datos legacy)
            // Los nuevos datos se cifrarán automáticamente al guardar
            return text;
        }
    }

    /**
     * Genera un IV aleatorio
     */
    private byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * Obtiene la clave secreta desde la configuración
     */
    private SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * Genera una nueva clave secreta AES-256 (usar solo para generar nueva clave)
     */
    public static String generateNewSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}
