package com.logistic.control.service;

import com.logistic.control.config.SifenConfig;
import com.logistic.control.entity.Cliente;
import com.logistic.control.entity.DetalleFactura;
import com.logistic.control.entity.Factura;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Servicio para generación de documentos electrónicos SIFEN en formato XML
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SifenXmlGenerator {

    private final SifenConfig sifenConfig;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Genera el XML del documento electrónico (DE) según especificaciones SIFEN
     */
    public String generarXmlFactura(Factura factura) {
        try {
            log.info("Generando XML para factura ID: {}", factura.getId());

            String cdc = generarCDC(factura);
            factura.setCdc(cdc);

            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xml.append("<rDE xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");

            // A. Campos firmados del DE
            xml.append("  <DE Id=\"").append(cdc).append("\">\n");
            
            // A1. Campos inherentes al DE
            generarCamposInherentes(xml, factura, cdc);
            
            // B. Datos del emisor
            generarDatosEmisor(xml);
            
            // C. Datos del receptor
            generarDatosReceptor(xml, factura.getCliente());
            
            // D. Campos que componen el Documento Electrónico
            generarCamposDocumento(xml, factura);
            
            // E. Items del documento
            generarItems(xml, factura);
            
            // F. Campos de subtotales y totales
            generarTotales(xml, factura);
            
            xml.append("  </DE>\n");
            xml.append("</rDE>");

            return xml.toString();

        } catch (Exception e) {
            log.error("Error generando XML de factura", e);
            throw new RuntimeException("Error generando XML: " + e.getMessage(), e);
        }
    }

    /**
     * Genera el CDC (Código de Control del Documento)
     * Formato: 44 dígitos = RUC(8) + DV(1) + Establecimiento(3) + Punto(3) + Tipo(2) + Número(7) + Fecha(8) + Tipo Emisión(1) + Código Seguridad(9) + DV(1)
     */
    private String generarCDC(Factura factura) {
        try {
            StringBuilder cdc = new StringBuilder();

            // RUC del emisor (8 dígitos) + DV (1 dígito)
            String ruc = sifenConfig.getRucEmisor();
            cdc.append(String.format("%8s", ruc.replace("-", "")).replace(' ', '0'));
            cdc.append(calcularDV(ruc));

            // Establecimiento (3 dígitos)
            cdc.append(String.format("%03d", Integer.parseInt(factura.getEstablecimiento())));

            // Punto de expedición (3 dígitos)
            cdc.append(String.format("%03d", Integer.parseInt(factura.getPuntoExpedicion())));

            // Tipo de documento (2 dígitos) - 01 = Factura Electrónica
            cdc.append(String.format("%02d", Integer.parseInt(factura.getTipoDocumento())));

            // Número de documento (7 dígitos)
            String[] partes = factura.getNumeroFactura().split("-");
            String numero = partes.length == 3 ? partes[2] : "0000001";
            cdc.append(String.format("%07d", Integer.parseInt(numero)));

            // Fecha (8 dígitos YYYYMMDD)
            cdc.append(factura.getFechaEmision().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            // Tipo de emisión (1 dígito) - 1 = Normal
            cdc.append("1");

            // Código de seguridad (9 dígitos aleatorios)
            Random random = new Random();
            for (int i = 0; i < 9; i++) {
                cdc.append(random.nextInt(10));
            }

            // Dígito verificador del CDC
            cdc.append(calcularDV(cdc.toString()));

            return cdc.toString();

        } catch (Exception e) {
            log.error("Error generando CDC", e);
            throw new RuntimeException("Error generando CDC", e);
        }
    }

    /**
     * Calcula el dígito verificador usando algoritmo módulo 11
     */
    private int calcularDV(String numero) {
        int[] factores = {2, 3, 4, 5, 6, 7, 2, 3, 4, 5, 6, 7, 2, 3, 4, 5, 6, 7, 2, 3, 4, 5, 6, 7, 2, 3, 4, 5, 6, 7, 2, 3, 4, 5, 6, 7, 2, 3, 4, 5, 6, 7, 2};
        int suma = 0;
        int len = numero.length();

        for (int i = 0; i < len; i++) {
            int digito = Character.getNumericValue(numero.charAt(i));
            suma += digito * factores[i % factores.length];
        }

        int resto = suma % 11;
        int dv = 11 - resto;

        if (dv == 11) return 0;
        if (dv == 10) return 1;
        return dv;
    }

    /**
     * Genera la sección A1: Campos inherentes al DE
     */
    private void generarCamposInherentes(StringBuilder xml, Factura factura, String cdc) {
        xml.append("    <gOpeDE>\n");
        xml.append("      <iTipEmi>1</iTipEmi>\n"); // 1 = Normal
        xml.append("      <dDesTipEmi>Normal</dDesTipEmi>\n");
        xml.append("      <dCodSeg>").append(cdc.substring(34, 43)).append("</dCodSeg>\n");
        xml.append("      <dInfoEmi>Información adicional</dInfoEmi>\n");
        xml.append("      <dInfoFisc>Información fiscal</dInfoFisc>\n");
        xml.append("    </gOpeDE>\n");

        xml.append("    <gTimb>\n");
        xml.append("      <iTiDE>1</iTiDE>\n"); // 1 = Factura Electrónica
        xml.append("      <dDesTiDE>Factura Electrónica</dDesTiDE>\n");
        xml.append("      <dNumTim>").append(factura.getTimbrado()).append("</dNumTim>\n");
        xml.append("      <dEst>").append(factura.getEstablecimiento()).append("</dEst>\n");
        xml.append("      <dPunExp>").append(factura.getPuntoExpedicion()).append("</dPunExp>\n");
        xml.append("      <dNumDoc>").append(obtenerNumeroDocumento(factura)).append("</dNumDoc>\n");
        xml.append("      <dFeIniT>").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</dFeIniT>\n");
        xml.append("    </gTimb>\n");

        xml.append("    <gDatGralOpe>\n");
        xml.append("      <dFeEmiDE>").append(factura.getFechaEmision().format(DATETIME_FORMATTER)).append("</dFeEmiDE>\n");
        xml.append("    </gDatGralOpe>\n");
    }

    /**
     * Genera la sección B: Datos del emisor
     */
    private void generarDatosEmisor(StringBuilder xml) {
        xml.append("    <gEmis>\n");
        xml.append("      <dRucEm>").append(sifenConfig.getRucEmisor()).append("</dRucEm>\n");
        xml.append("      <dDVEmi>").append(calcularDV(sifenConfig.getRucEmisor())).append("</dDVEmi>\n");
        xml.append("      <iTipCont>1</iTipCont>\n"); // 1 = Persona Física
        xml.append("      <cTipReg>8</cTipReg>\n"); // 8 = Varios
        xml.append("      <dNomEmi>").append(escaparXml(sifenConfig.getRazonSocialEmisor())).append("</dNomEmi>\n");
        
        if (sifenConfig.getNombreFantasia() != null) {
            xml.append("      <dNomFanEmi>").append(escaparXml(sifenConfig.getNombreFantasia())).append("</dNomFanEmi>\n");
        }
        
        xml.append("      <dDirEmi>").append(escaparXml(sifenConfig.getDireccion())).append("</dDirEmi>\n");
        xml.append("      <dNumCas>0</dNumCas>\n");
        xml.append("      <dCompDir1>Barrio</dCompDir1>\n");
        xml.append("      <dCompDir2>Complemento</dCompDir2>\n");
        xml.append("      <cDepEmi>").append(sifenConfig.getDepartamento()).append("</cDepEmi>\n");
        xml.append("      <dDesDepEmi>").append(sifenConfig.getCiudad()).append("</dDesDepEmi>\n");
        xml.append("      <cCiuEmi>1</cCiuEmi>\n");
        xml.append("      <dDesCiuEmi>").append(sifenConfig.getCiudad()).append("</dDesCiuEmi>\n");
        xml.append("      <dTelEmi>").append(sifenConfig.getTelefono()).append("</dTelEmi>\n");
        xml.append("      <dEmailE>").append(sifenConfig.getEmail()).append("</dEmailE>\n");
        xml.append("    </gEmis>\n");
    }

    /**
     * Genera la sección C: Datos del receptor
     */
    private void generarDatosReceptor(StringBuilder xml, Cliente cliente) {
        xml.append("    <gDatRec>\n");
        xml.append("      <iNatRec>1</iNatRec>\n"); // 1 = No contribuyente
        xml.append("      <iTiOpe>1</iTiOpe>\n"); // 1 = B2C
        xml.append("      <cPaisRec>PRY</cPaisRec>\n");
        xml.append("      <dDesPaisRe>Paraguay</dDesPaisRe>\n");
        xml.append("      <iTiContRec>1</iTiContRec>\n");
        
        if (cliente.getRuc() != null && !cliente.getRuc().isEmpty()) {
            xml.append("      <dRucRec>").append(cliente.getRuc()).append("</dRucRec>\n");
            xml.append("      <dDVRec>").append(calcularDV(cliente.getRuc())).append("</dDVRec>\n");
        }
        
        xml.append("      <dNomRec>").append(escaparXml(cliente.getRazonSocial())).append("</dNomRec>\n");
        
        if (cliente.getDireccion() != null) {
            xml.append("      <dDirRec>").append(escaparXml(cliente.getDireccion())).append("</dDirRec>\n");
        }
        
        if (cliente.getTelefono() != null) {
            xml.append("      <dTelRec>").append(cliente.getTelefono()).append("</dTelRec>\n");
        }
        
        if (cliente.getEmail() != null) {
            xml.append("      <dEmailRec>").append(cliente.getEmail()).append("</dEmailRec>\n");
        }
        
        xml.append("    </gDatRec>\n");
    }

    /**
     * Genera la sección D: Campos que componen el documento
     */
    private void generarCamposDocumento(StringBuilder xml, Factura factura) {
        xml.append("    <gDtipDE>\n");
        xml.append("      <gCamFE>\n");
        xml.append("        <iIndPres>1</iIndPres>\n"); // 1 = Operación presencial
        xml.append("        <dDesIndPres>Presencial</dDesIndPres>\n");
        xml.append("        <dFecEmNR></dFecEmNR>\n");
        
        // Condición de la operación
        xml.append("        <gCompPub>\n");
        xml.append("          <dModCont>1</dModCont>\n"); // 1 = Contratación pública
        xml.append("          <dEntCont></dEntCont>\n");
        xml.append("          <dAnoCont></dAnoCont>\n");
        xml.append("          <dSecCont></dSecCont>\n");
        xml.append("          <dFeCodCont></dFeCodCont>\n");
        xml.append("        </gCompPub>\n");
        
        xml.append("      </gCamFE>\n");
        xml.append("    </gDtipDE>\n");

        // Condición de la operación y pagos
        xml.append("    <gTotSub>\n");
        xml.append("      <dSubExe>0</dSubExe>\n"); // Subtotal exento
        xml.append("      <dSubExo>0</dSubExo>\n"); // Subtotal exonerado
        xml.append("      <dSub5>").append(calcularBase5(factura)).append("</dSub5>\n");
        xml.append("      <dSub10>").append(calcularBase10(factura)).append("</dSub10>\n");
        xml.append("      <dTotOpe>").append(factura.getSubtotal()).append("</dTotOpe>\n");
        xml.append("      <dTotDesc>").append(factura.getDescuento()).append("</dTotDesc>\n");
        xml.append("      <dTotDescGlotem>0</dTotDescGlotem>\n");
        xml.append("      <dTotAntItem>0</dTotAntItem>\n");
        xml.append("      <dTotAnt>0</dTotAnt>\n");
        xml.append("      <dPorcDescTotal>0</dPorcDescTotal>\n");
        xml.append("      <dDescTotal>0</dDescTotal>\n");
        xml.append("      <dAnticipo>0</dAnticipo>\n");
        xml.append("      <dRedon>0</dRedon>\n");
        xml.append("      <dComi>0</dComi>\n");
        xml.append("      <dTotGralOpe>").append(factura.getTotal()).append("</dTotGralOpe>\n");
        xml.append("      <dIVA5>").append(factura.getIva5()).append("</dIVA5>\n");
        xml.append("      <dIVA10>").append(factura.getIva10()).append("</dIVA10>\n");
        xml.append("      <dLiqTotIVA5>").append(factura.getIva5()).append("</dLiqTotIVA5>\n");
        xml.append("      <dLiqTotIVA10>").append(factura.getIva10()).append("</dLiqTotIVA10>\n");
        xml.append("      <dTotIVA>").append(factura.getTotalIva()).append("</dTotIVA>\n");
        xml.append("      <dBaseGrav5>").append(calcularBase5(factura)).append("</dBaseGrav5>\n");
        xml.append("      <dBaseGrav10>").append(calcularBase10(factura)).append("</dBaseGrav10>\n");
        xml.append("      <dTBasGraIVA>").append(factura.getSubtotal()).append("</dTBasGraIVA>\n");
        xml.append("    </gTotSub>\n");
    }

    /**
     * Genera la sección E: Items del documento
     */
    private void generarItems(StringBuilder xml, Factura factura) {
        int itemNum = 1;
        for (DetalleFactura detalle : factura.getDetalles()) {
            xml.append("    <gCamItem>\n");
            xml.append("      <dNroItem>").append(itemNum++).append("</dNroItem>\n");
            xml.append("      <dDesProSer>").append(escaparXml(detalle.getDescripcion())).append("</dDesProSer>\n");
            xml.append("      <dCantProSer>").append(detalle.getCantidad()).append("</dCantProSer>\n");
            xml.append("      <cUniMed>77</cUniMed>\n"); // 77 = Unidad
            xml.append("      <dDesUniMed>Unidad</dDesUniMed>\n");
            xml.append("      <dPUniProSer>").append(detalle.getPrecioUnitario()).append("</dPUniProSer>\n");
            xml.append("      <dTiCamIt>1</dTiCamIt>\n");
            xml.append("      <dTotBruOpeItem>").append(detalle.getTotal()).append("</dTotBruOpeItem>\n");
            
            // IVA del item
            int tasaIva = detalle.getPorcentajeIva() != null ? detalle.getPorcentajeIva() : 10;
            xml.append("      <gValorItem>\n");
            xml.append("        <dTotOpeItem>").append(detalle.getTotal()).append("</dTotOpeItem>\n");
            xml.append("      </gValorItem>\n");
            
            xml.append("      <gCamIVA>\n");
            xml.append("        <iAfecIVA>1</iAfecIVA>\n"); // 1 = Gravado IVA
            xml.append("        <dDesAfecIVA>Gravado IVA</dDesAfecIVA>\n");
            xml.append("        <dPropIVA>100</dPropIVA>\n");
            xml.append("        <dTasaIVA>").append(tasaIva).append("</dTasaIVA>\n");
            xml.append("        <dBasGravIVA>").append(detalle.getTotal()).append("</dBasGravIVA>\n");
            xml.append("        <dLiqIVAItem>").append(detalle.getTotal() * tasaIva / 100).append("</dLiqIVAItem>\n");
            xml.append("      </gCamIVA>\n");
            
            xml.append("    </gCamItem>\n");
        }
    }

    /**
     * Genera la sección F: Totales
     */
    private void generarTotales(StringBuilder xml, Factura factura) {
        xml.append("    <gPago>\n");
        xml.append("      <iPagCont>1</iPagCont>\n"); // 1 = Contado
        xml.append("      <dDesPagCont>Contado</dDesPagCont>\n");
        xml.append("      <gPagCont>\n");
        xml.append("        <gPagCheq>\n");
        xml.append("          <dMonEnt>").append(factura.getTotal()).append("</dMonEnt>\n");
        xml.append("        </gPagCheq>\n");
        xml.append("      </gPagCont>\n");
        xml.append("    </gPago>\n");
    }

    private String obtenerNumeroDocumento(Factura factura) {
        String[] partes = factura.getNumeroFactura().split("-");
        return partes.length == 3 ? partes[2] : "0000001";
    }

    private double calcularBase5(Factura factura) {
        return factura.getDetalles().stream()
                .filter(d -> d.getPorcentajeIva() != null && d.getPorcentajeIva() == 5)
                .mapToDouble(DetalleFactura::getTotal)
                .sum();
    }

    private double calcularBase10(Factura factura) {
        return factura.getDetalles().stream()
                .filter(d -> d.getPorcentajeIva() != null && d.getPorcentajeIva() == 10)
                .mapToDouble(DetalleFactura::getTotal)
                .sum();
    }

    private String escaparXml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
