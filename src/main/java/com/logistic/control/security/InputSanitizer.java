package com.logistic.control.security;

import org.owasp.encoder.Encode;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Componente para sanitización de inputs contra inyecciones y XSS
 * Implementa protecciones según OWASP Top 10
 */
@Component
public class InputSanitizer {

    // Patrones para detección de ataques
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "('.+(--|#|;))|" +
            "(union\\s+select)|" +
            "(insert\\s+into)|" +
            "(delete\\s+from)|" +
            "(drop\\s+table)|" +
            "(update\\s+.+set)|" +
            "(exec\\s*\\()|" +
            "(execute\\s*\\()|" +
            "(script\\s*>)|" +
            "(javascript:)|" +
            "(onerror\\s*=)|" +
            "(onload\\s*=)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern LDAP_INJECTION_PATTERN = Pattern.compile(
            "[\\*\\(\\)\\\\\\x00]"
    );

    private static final Pattern OS_COMMAND_INJECTION_PATTERN = Pattern.compile(
            "(;|\\||&|\\$|`|\\n|\\r|\\(|\\)|<|>|\\{|\\}|\\[|\\])"
    );

    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
            "(\\.\\./)|(\\.\\\\)|(\\%2e\\%2e)|(\\.\\./\\.\\./)|(/etc/)|(/var/)|(/usr/)|(/sys/)|(/proc/)|(/dev/)",
            Pattern.CASE_INSENSITIVE
    );

    // XSS patterns
    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(<script[^>]*>.*?</script>)|" +
            "(<iframe[^>]*>.*?</iframe>)|" +
            "(javascript:)|" +
            "(on\\w+\\s*=)|" +
            "(<embed[^>]*>)|" +
            "(<object[^>]*>)|" +
            "(<applet[^>]*>)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /**
     * Sanitiza input general removiendo caracteres peligrosos
     */
    public String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Encode para HTML (previene XSS)
        String sanitized = Encode.forHtml(input.trim());

        // Validar que no contenga patrones maliciosos
        if (containsMaliciousPattern(sanitized)) {
            throw new SecurityException("Input contiene patrones sospechosos");
        }

        return sanitized;
    }

    /**
     * Sanitiza input específicamente para prevenir SQL Injection
     */
    public String sanitizeForSql(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            throw new SecurityException("Input contiene patrones de SQL Injection");
        }

        // Escapar comillas simples y dobles
        return input.replace("'", "''")
                    .replace("\"", "\\\"")
                    .replace("\\", "\\\\");
    }

    /**
     * Sanitiza paths para prevenir Directory Traversal
     */
    public String sanitizePath(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }

        if (PATH_TRAVERSAL_PATTERN.matcher(path).find()) {
            throw new SecurityException("Path contiene patrones de Directory Traversal");
        }

        // Normalizar y validar path
        String normalized = path.replaceAll("[/\\\\]+", "/")
                               .replaceAll("^\\.+", "")
                               .replaceAll("\\.+$", "");

        if (normalized.contains("..")) {
            throw new SecurityException("Path inválido");
        }

        return normalized;
    }

    /**
     * Sanitiza para prevenir LDAP Injection
     */
    public String sanitizeForLdap(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        if (LDAP_INJECTION_PATTERN.matcher(input).find()) {
            throw new SecurityException("Input contiene caracteres LDAP inválidos");
        }

        return input.replace("\\", "\\\\")
                    .replace("*", "\\*")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace("\u0000", "\\00");
    }

    /**
     * Sanitiza para prevenir OS Command Injection
     */
    public String sanitizeForCommand(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        if (OS_COMMAND_INJECTION_PATTERN.matcher(input).find()) {
            throw new SecurityException("Input contiene caracteres peligrosos para comandos");
        }

        return input;
    }

    /**
     * Sanitiza para prevenir XSS
     */
    public String sanitizeForXss(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        if (XSS_PATTERN.matcher(input).find()) {
            throw new SecurityException("Input contiene patrones XSS");
        }

        // Encode para diferentes contextos
        return Encode.forHtmlContent(input);
    }

    /**
     * Valida email de forma segura
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        Pattern emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );

        return emailPattern.matcher(email).matches() && email.length() <= 254;
    }

    /**
     * Valida RUC paraguayo
     */
    public boolean isValidRuc(String ruc) {
        if (ruc == null || ruc.isEmpty()) {
            return false;
        }

        // Accept both formats: with DV (12345678-9) or only number (12345678)
        Pattern rucPattern = Pattern.compile("^\\d{6,8}(-\\d{1})?$");
        return rucPattern.matcher(ruc).matches();
    }

    /**
     * Verifica si el input contiene patrones maliciosos
     */
    private boolean containsMaliciousPattern(String input) {
        return SQL_INJECTION_PATTERN.matcher(input).find() ||
               XSS_PATTERN.matcher(input).find() ||
               PATH_TRAVERSAL_PATTERN.matcher(input).find() ||
               OS_COMMAND_INJECTION_PATTERN.matcher(input).find();
    }

    /**
     * Trunca string a longitud máxima (previene DoS por input largo)
     */
    public String truncate(String input, int maxLength) {
        if (input == null) {
            return null;
        }

        return input.length() > maxLength ? input.substring(0, maxLength) : input;
    }

    /**
     * Valida que el string solo contenga caracteres alfanuméricos
     */
    public boolean isAlphanumeric(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        return input.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Valida número de teléfono paraguayo
     */
    public boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }

        // Formatos: +595981234567, 0981234567, 981234567
        Pattern phonePattern = Pattern.compile(
            "^(\\+595|0)?[9][0-9]{8}$"
        );

        return phonePattern.matcher(phone.replaceAll("[\\s-]", "")).matches();
    }
}
