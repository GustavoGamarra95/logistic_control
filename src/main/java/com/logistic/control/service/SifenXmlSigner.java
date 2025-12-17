package com.logistic.control.service;

import com.logistic.control.config.SifenConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

/**
 * Servicio para firma digital de documentos XML según XAdES-BES
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SifenXmlSigner {

    private final SifenConfig sifenConfig;

    /**
     * Firma digitalmente el XML del documento electrónico con XAdES-BES
     */
    public String firmarXml(String xmlSinFirmar) {
        try {
            log.info("Firmando XML del documento electrónico...");

            // Cargar el certificado
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(sifenConfig.getCertificatePath())) {
                keyStore.load(fis, sifenConfig.getCertificatePassword().toCharArray());
            }

            String alias = keyStore.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, 
                    sifenConfig.getCertificatePassword().toCharArray());
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);

            // Parsear el XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlSinFirmar.getBytes("UTF-8")));

            // Obtener el elemento DE (que tiene el atributo Id)
            Element deElement = (Element) doc.getElementsByTagName("DE").item(0);
            String id = deElement.getAttribute("Id");
            
            if (id == null || id.isEmpty()) {
                throw new RuntimeException("El elemento DE no tiene atributo Id");
            }

            // Crear la firma XML
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            // Crear la referencia al elemento a firmar
            Reference ref = fac.newReference(
                    "#" + id,
                    fac.newDigestMethod(DigestMethod.SHA256, null),
                    Collections.singletonList(
                            fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)
                    ),
                    null,
                    null
            );

            // Crear la información de firma
            SignedInfo si = fac.newSignedInfo(
                    fac.newCanonicalizationMethod(
                            CanonicalizationMethod.INCLUSIVE,
                            (C14NMethodParameterSpec) null
                    ),
                    fac.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                    Collections.singletonList(ref)
            );

            // Crear KeyInfo con el certificado
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            List<Object> x509Content = List.of(certificate.getSubjectX500Principal().getName(), certificate);
            X509Data xd = kif.newX509Data(x509Content);
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

            // Crear el elemento Signature
            DOMSignContext dsc = new DOMSignContext(privateKey, deElement);
            XMLSignature signature = fac.newXMLSignature(si, ki);
            
            // Firmar el documento
            signature.sign(dsc);

            // Convertir el documento firmado a String
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            StringWriter sw = new StringWriter();
            trans.transform(new DOMSource(doc), new StreamResult(sw));

            String xmlFirmado = sw.toString();
            log.info("XML firmado exitosamente");
            
            return xmlFirmado;

        } catch (java.security.GeneralSecurityException | java.io.IOException | 
                 javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException |
                 javax.xml.crypto.MarshalException | javax.xml.crypto.dsig.XMLSignatureException |
                 javax.xml.transform.TransformerException e) {
            log.error("Error firmando XML", e);
            throw new RuntimeException("Error firmando XML: " + e.getMessage(), e);
        }
    }

    /**
     * Valida la firma digital de un XML
     */
    public boolean validarFirma(String xmlFirmado) {
        try {
            log.info("Validando firma del XML...");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlFirmado.getBytes("UTF-8")));

            // Buscar el elemento Signature
            Element signatureElement = (Element) doc.getElementsByTagNameNS(
                    XMLSignature.XMLNS, "Signature").item(0);

            if (signatureElement == null) {
                log.warn("No se encontró elemento Signature en el XML");
                return false;
            }

            // Crear el contexto de validación
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            DOMValidateContext valContext = 
                    new DOMValidateContext(
                            new X509KeySelector(), signatureElement);

            // Validar la firma
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);
            boolean isValid = signature.validate(valContext);

            if (isValid) {
                log.info("Firma válida");
            } else {
                log.warn("Firma inválida");
            }

            return isValid;

        } catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException |
                 java.io.IOException | javax.xml.crypto.MarshalException |
                 javax.xml.crypto.dsig.XMLSignatureException e) {
            log.error("Error validando firma XML", e);
            return false;
        }
    }

    /**
     * KeySelector para validación de firma
     */
    private static class X509KeySelector extends javax.xml.crypto.KeySelector {
        @Override
        public javax.xml.crypto.KeySelectorResult select(
                KeyInfo keyInfo,
                javax.xml.crypto.KeySelector.Purpose purpose,
                javax.xml.crypto.AlgorithmMethod method,
                javax.xml.crypto.XMLCryptoContext context)
                throws javax.xml.crypto.KeySelectorException {

            if (keyInfo == null) {
                throw new javax.xml.crypto.KeySelectorException("KeyInfo es null");
            }

            List<?> list = keyInfo.getContent();
            for (Object obj : list) {
                if (obj instanceof X509Data x509Data) {
                    for (Object content : x509Data.getContent()) {
                        if (content instanceof X509Certificate cert) {
                            return () -> cert.getPublicKey();
                        }
                    }
                }
            }

            throw new javax.xml.crypto.KeySelectorException(
                    "No se encontró certificado X509 en KeyInfo");
        }
    }
}
