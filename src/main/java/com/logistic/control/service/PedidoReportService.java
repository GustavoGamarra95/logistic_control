package com.logistic.control.service;

import com.google.zxing.WriterException;
import com.logistic.control.entity.DetallePedido;
import com.logistic.control.entity.Pedido;
import com.logistic.control.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para generación de reportes de Pedidos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoReportService {

    private final ReportService reportService;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public byte[] generatePedidoReport(Long pedidoId) throws JRException, IOException, WriterException {
        log.info("Generando reporte de pedido ID: {}", pedidoId);

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Preparar parámetros
        Map<String, Object> parameters = reportService.createBaseParameters();
        parameters.put("PEDIDO_NUMERO", pedido.getCodigoTracking());
        parameters.put("CLIENTE_RAZON_SOCIAL", pedido.getCliente().getRazonSocial());
        parameters.put("CLIENTE_RUC", pedido.getCliente().getRuc() + "-" + pedido.getCliente().getDv());
        parameters.put("FECHA_PEDIDO", pedido.getFechaRegistro());
        parameters.put("ESTADO", pedido.getEstado().toString());
        parameters.put("TOTAL", pedido.getTotal());

        // Preparar datos de detalles
        List<Map<String, Object>> detalles = pedido.getDetalles().stream()
                .map(this::convertDetalleToData)
                .collect(Collectors.toList());

        // Generar reporte con QR
        return reportService.generatePdfReportWithQR(
                "pedido",
                parameters,
                detalles,
                "PEDIDO",
                pedidoId
        );
    }

    private Map<String, Object> convertDetalleToData(DetallePedido detalle) {
        Map<String, Object> data = new HashMap<>();
        data.put("producto", detalle.getProducto().getDescripcion());
        data.put("cantidad", detalle.getCantidad());
        data.put("precioUnitario", detalle.getPrecioUnitario());
        data.put("subtotal", detalle.getSubTotal());
        return data;
    }
}
