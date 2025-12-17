package com.logistic.control.service;

import com.google.zxing.WriterException;
import com.logistic.control.entity.Producto;
import com.logistic.control.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioReportService {

    private final ReportService reportService;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public byte[] generateInventarioReport(String categoria) throws JRException, IOException, WriterException {
        List<Producto> productos = productoRepository.findAll();

        // Filtrar por categoría si se especifica
        if (categoria != null && !categoria.isEmpty()) {
            productos = productos.stream()
                    .filter(p -> categoria.equalsIgnoreCase("TODOS") || true) // Sin filtro real por ahora
                    .collect(Collectors.toList());
        }

        Map<String, Object> parameters = reportService.createBaseParameters();
        parameters.put("FECHA_REPORTE", new Date());
        parameters.put("TITULO", categoria != null ? "Inventario - " + categoria : "Inventario Completo");
        parameters.put("TOTAL_ITEMS", productos.size());

        // Calcular valor total (usando precio de venta como estimado)
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Producto p : productos) {
            // Como no hay campo precio directo, usar 0
            valorTotal = valorTotal.add(BigDecimal.ZERO);
        }
        parameters.put("VALOR_TOTAL", valorTotal);

        List<Map<String, Object>> datos = productos.stream()
                .map(p -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("producto", p.getDescripcion());
                    data.put("sku", p.getCodigo());
                    data.put("categoria", "GENERAL"); // Categoría por defecto
                    data.put("stockDisponible", 0); // No hay campo stock
                    data.put("stockMinimo", 0);
                    data.put("precioUnitario", 0.0);
                    data.put("valorTotal", 0.0);
                    data.put("ubicacion", "DEPOSITO"); // Ubicación por defecto
                    return data;
                }).collect(Collectors.toList());

        return reportService.generatePdfReportWithQR("inventario", parameters, datos, "INVENTARIO", System.currentTimeMillis());
    }
}
