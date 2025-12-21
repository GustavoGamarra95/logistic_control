package com.logistic.control.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static EncryptionUtil encryptionUtil;

    @Autowired
    public void setEncryptionUtil(EncryptionUtil util) {
        AttributeEncryptor.encryptionUtil = util;
    }
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return attribute;
        }
        return encryptionUtil.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return dbData;
        }
        // Intentar descifrar, si falla asumir que es texto plano (datos legacy)
        return encryptionUtil.decryptOrReturnPlainText(dbData);
    }
}
