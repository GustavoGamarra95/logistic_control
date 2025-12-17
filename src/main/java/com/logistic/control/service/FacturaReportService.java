package com.logistic.control.service;

import com.google.zxing.WriterException;
import com.logistic.control.entity.DetalleFactura;
import com.logistic.control.entity.Factura;
import com.logistic.control.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacturaReportService {

    private final ReportService reportService;
    private final FacturaRepository facturaRepository;

    @Transactional(readOnly = true)
    public byte[] generateFacturaReport(Long facturaId) throws JRException, IOException, WriterException {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        Map<String, Object> parameters = reportService.createBaseParameters();
        parameters.put("FACTURA_NUMERO", factura.getNumeroFactura());
        parameters.put("TIMBRADO", factura.getTimbrado() != null ? factura.getTimbrado() : "N/A");
        parameters.put("CLIENTE_RAZON_SOCIAL", factura.getCliente().getRazonSocial());
        parameters.put("CLIENTE_RUC", factura.getCliente().getRuc() + "-" + factura.getCliente().getDv());
        parameters.put("FECHA_EMISION", factura.getFechaEmision());
        parameters.put("CONDICION_VENTA", "CONTADO"); // Por defecto
        parameters.put("SUBTOTAL", factura.getSubtotal());
        parameters.put("IVA", factura.getTotalIva());
        parameters.put("TOTAL", factura.getTotal());
        parameters.put("CDC", factura.getCdc() != null ? factura.getCdc() : "N/A");

        List<Map<String, Object>> detalles = factura.getDetalles().stream()
                .map(d -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("descripcion", d.getDescripcion());
                    data.put("cantidad", d.getCantidad());
                    data.put("precioUnitario", d.getPrecioUnitario());
                    // Simplificado: calculamos basado en porcentaje IVA
                    if (d.getPorcentajeIva() == null || d.getPorcentajeIva() == 0) {
                        data.put("exentas", d.getSubtotal());
                        data.put("iva5", 0.0);
                        data.put("iva10", 0.0);
                    } else if (d.getPorcentajeIva() == 5) {
                        data.put("exentas", 0.0);
                        data.put("iva5", d.getSubtotal());
                        data.put("iva10", 0.0);
                    } else {
                        data.put("exentas", 0.0);
                        data.put("iva5", 0.0);
                        data.put("iva10", d.getSubtotal());
                    }
                    return data;
                }).collect(Collectors.toList());

        return reportService.generatePdfReportWithQR("factura", parameters, detalles, "FACTURA", facturaId);
    }
}
