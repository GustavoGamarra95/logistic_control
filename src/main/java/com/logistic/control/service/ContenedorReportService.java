package com.logistic.control.service;

import com.google.zxing.WriterException;
import com.logistic.control.entity.Container;
import com.logistic.control.repository.ContainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContenedorReportService {

    private final ReportService reportService;
    private final ContainerRepository containerRepository;

    @Transactional(readOnly = true)
    public byte[] generateContenedorReport(Long contenedorId) throws JRException, IOException, WriterException {
        Container container = containerRepository.findById(contenedorId)
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));

        Map<String, Object> parameters = reportService.createBaseParameters();
        parameters.put("CONTENEDOR_NUMERO", container.getNumero());
        parameters.put("TIPO", container.getTipo().toString());
        parameters.put("ESTADO", "EN_TRANSITO"); // Estado por defecto
        parameters.put("PESO_ACTUAL", container.getPesoKg() != null ? container.getPesoKg() : 0.0);
        parameters.put("CAPACIDAD_PESO", container.getPesoMaximoKg() != null ? container.getPesoMaximoKg() : 0.0);
        parameters.put("VOLUMEN_ACTUAL", container.getVolumenM3() != null ? container.getVolumenM3() : 0.0);
        parameters.put("CAPACIDAD_VOLUMEN", container.getVolumenMaximoM3() != null ? container.getVolumenMaximoM3() : 0.0);
        parameters.put("UBICACION", container.getPuertoOrigen() != null ? container.getPuertoOrigen() : "N/A");
        parameters.put("FECHA_LLEGADA", container.getFechaSalida());

        // Para este ejemplo, crearemos datos ficticios de productos
        // En producción, esto vendría de una tabla de relación container_productos
        List<Map<String, Object>> productos = List.of(
                Map.of("producto", "Carga general", "cantidad", 1, "pesoKg", container.getPesoKg() != null ? container.getPesoKg() : 0.0, "volumenM3", container.getVolumenM3() != null ? container.getVolumenM3() : 0.0)
        );

        return reportService.generatePdfReportWithQR("contenedor", parameters, productos, "CONTENEDOR", contenedorId);
    }
}
