package com.logistic.control.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.control.dto.request.ContainerRequest;
import com.logistic.control.dto.response.ContainerResponse;
import com.logistic.control.entity.Container;
import com.logistic.control.entity.Producto;
import com.logistic.control.exception.BusinessException;
import com.logistic.control.exception.InvalidStateException;
import com.logistic.control.exception.ResourceNotFoundException;
import com.logistic.control.repository.ContainerRepository;
import com.logistic.control.repository.ProductoRepository;
import com.logistic.control.security.InputSanitizer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestión de Containers
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContainerService {

    private final ContainerRepository containerRepository;
    private final ProductoRepository productoRepository;
    private final InputSanitizer inputSanitizer;

    /**
     * Listar todos los containers con paginación
     */
    public Page<ContainerResponse> listarContainers(Pageable pageable) {
        log.debug("Listando containers - página: {}", pageable.getPageNumber());
        return containerRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtener container por ID
     */
    public ContainerResponse obtenerContainer(Long id) {
        log.debug("Obteniendo container con ID: {}", id);
        Container container = containerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Container", "id", id));
        return toResponse(container);
    }

    /**
     * Buscar container por número
     */
    public ContainerResponse buscarPorNumero(String numeroContainer) {
        String numeroSanitized = inputSanitizer.sanitize(numeroContainer);
        log.debug("Buscando container por número: {}", numeroSanitized);
        
        Container container = containerRepository.findByNumero(numeroSanitized)
                .orElseThrow(() -> new ResourceNotFoundException("Container", "número", numeroSanitized));
        return toResponse(container);
    }

    /**
     * Crear nuevo container
     */
    @Transactional
    public ContainerResponse crearContainer(ContainerRequest request) {
        log.info("Creando nuevo container: {}", request.getNumero());
        
        // Validar duplicados
        if (containerRepository.findByNumero(request.getNumero()).isPresent()) {
            throw new BusinessException("Ya existe un container con ese número");
        }
        
        // Validar datos
        validarDatosContainer(request);
        
        // Crear entidad
        Container container = Container.builder()
                .numero(inputSanitizer.sanitize(request.getNumero()))
                .tipo(request.getTipo())
                .fechaSalida(request.getFechaSalida())
                .empresaNaviera(inputSanitizer.sanitize(request.getEmpresaNaviera()))
                .buqueNombre(inputSanitizer.sanitize(request.getBuqueNombre()))
                .puertoOrigen(inputSanitizer.sanitize(request.getPuertoOrigen()))
                .puertoDestino(inputSanitizer.sanitize(request.getPuertoDestino()))
                .numeroBl(inputSanitizer.sanitize(request.getNumeroBl()))
                .pesoKg(0.0)
                .volumenM3(0.0)
                .consolidado(false)
                .observaciones(inputSanitizer.sanitize(request.getObservaciones()))
                .build();
        
        Container saved = containerRepository.save(container);
        log.info("Container creado exitosamente con ID: {}", saved.getId());
        
        return toResponse(saved);
    }

    /**
     * Actualizar container
     */
    @Transactional
    public ContainerResponse actualizarContainer(Long id, ContainerRequest request) {
        log.info("Actualizando container ID: {}", id);
        
        Container container = containerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Container", "id", id));
        
        // Validar que no se cambie el número a uno existente
        if (!container.getNumero().equals(request.getNumero())) {
            if (containerRepository.findByNumero(request.getNumero()).isPresent()) {
                throw new BusinessException("Ya existe un container con ese número");
            }
        }
        
        validarDatosContainer(request);
        
        // Actualizar campos
        container.setNumero(inputSanitizer.sanitize(request.getNumero()));
        container.setTipo(request.getTipo());
        container.setFechaSalida(request.getFechaSalida());
        container.setEmpresaNaviera(inputSanitizer.sanitize(request.getEmpresaNaviera()));
        container.setBuqueNombre(inputSanitizer.sanitize(request.getBuqueNombre()));
        container.setPuertoOrigen(inputSanitizer.sanitize(request.getPuertoOrigen()));
        container.setPuertoDestino(inputSanitizer.sanitize(request.getPuertoDestino()));
        container.setNumeroBl(inputSanitizer.sanitize(request.getNumeroBl()));
        container.setObservaciones(inputSanitizer.sanitize(request.getObservaciones()));
        
        Container updated = containerRepository.save(container);
        log.info("Container actualizado exitosamente: {}", updated.getId());
        
        return toResponse(updated);
    }

    /**
     * Consolidar productos en container
     */
    @Transactional
    public ContainerResponse consolidarProductos(Long containerId, List<Long> productosIds) {
        log.info("Consolidando productos en container ID: {}", containerId);
        
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ResourceNotFoundException("Container", "id", containerId));
        
        if (container.getConsolidado()) {
            throw new InvalidStateException("El container ya está consolidado");
        }
        
        // Agregar productos
        for (Long productoId : productosIds) {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));
            
            container.addProducto(producto);
        }
        
        // Calcular peso y volumen automáticamente
        container.consolidar();
        
        Container updated = containerRepository.save(container);
        log.info("Container consolidado exitosamente con {} productos", container.getProductos().size());
        
        return toResponse(updated);
    }

    /**
     * Desconsolidar container (liberar productos)
     */
    @Transactional
    public ContainerResponse desconsolidar(Long containerId) {
        log.info("Desconsolidando container ID: {}", containerId);
        
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new ResourceNotFoundException("Container", "id", containerId));
        
        if (!container.getConsolidado()) {
            throw new InvalidStateException("El container no está consolidado");
        }
        
        // Liberar productos
        container.getProductos().clear();
        
        container.setPesoKg(0.0);
        container.setVolumenM3(0.0);
        container.setConsolidado(false);
        
        Container updated = containerRepository.save(container);
        log.info("Container desconsolidado exitosamente");
        
        return toResponse(updated);
    }

    /**
     * Validar datos del container
     */
    private void validarDatosContainer(ContainerRequest request) {
        if (request.getFechaSalida() != null && 
            request.getFechaSalida().isBefore(LocalDate.now().minusYears(1))) {
            throw new BusinessException("La fecha de salida no puede ser anterior a 1 año");
        }
    }

    /**
     * Convertir entidad a DTO de respuesta
     */
    private ContainerResponse toResponse(Container container) {
        return ContainerResponse.builder()
                .id(container.getId())
                .numero(container.getNumero())
                .tipo(container.getTipo())
                .estado(calcularEstado(container))
                .pesoKg(container.getPesoKg())
                .pesoMaximoKg(container.getPesoMaximoKg())
                .volumenM3(container.getVolumenM3())
                .volumenMaximoM3(container.getVolumenMaximoM3())
                .empresaTransporte(container.getEmpresaTransporte())
                .empresaNaviera(container.getEmpresaNaviera())
                .buqueNombre(container.getBuqueNombre())
                .viajeNumero(container.getViajeNumero())
                .ruta(container.getRuta())
                .puertoOrigen(container.getPuertoOrigen())
                .puertoDestino(container.getPuertoDestino())
                .fechaSalida(container.getFechaSalida())
                .fechaLlegadaEstimada(container.getFechaLlegadaEstimada())
                .fechaLlegadaReal(container.getFechaLlegadaReal())
                .consolidado(container.getConsolidado())
                .enTransito(container.getEnTransito())
                .enPuerto(container.getEnPuerto())
                .enAduana(container.getEnAduana())
                .liberado(container.getLiberado())
                .numeroBl(container.getNumeroBl())
                .fechaEmisionBl(container.getFechaEmisionBl())
                .porcentajeOcupacionPeso(calcularPorcentajeOcupacionPeso(container))
                .porcentajeOcupacionVolumen(calcularPorcentajeOcupacionVolumen(container))
                .observaciones(container.getObservaciones())
                .createdAt(container.getCreatedAt())
                .updatedAt(container.getUpdatedAt())
                .build();
    }

    /**
     * Calcular estado del contenedor basado en flags booleanos
     */
    private String calcularEstado(Container container) {
        if (Boolean.TRUE.equals(container.getLiberado())) {
            return "DESPACHADO";
        }
        if (Boolean.TRUE.equals(container.getEnAduana())) {
            return "EN_ADUANA";
        }
        if (Boolean.TRUE.equals(container.getEnPuerto())) {
            return "EN_PUERTO";
        }
        if (Boolean.TRUE.equals(container.getEnTransito())) {
            return "EN_TRANSITO";
        }
        if (Boolean.TRUE.equals(container.getConsolidado())) {
            return "CERRADO";
        }
        return "EN_CONSOLIDACION";
    }

    /**
     * Calcular porcentaje de ocupación de peso
     */
    private Double calcularPorcentajeOcupacionPeso(Container container) {
        if (container.getPesoMaximoKg() != null && container.getPesoMaximoKg() > 0 && container.getPesoKg() != null) {
            return (container.getPesoKg() / container.getPesoMaximoKg()) * 100;
        }
        return 0.0;
    }

    /**
     * Calcular porcentaje de ocupación de volumen
     */
    private Double calcularPorcentajeOcupacionVolumen(Container container) {
        if (container.getVolumenMaximoM3() != null && container.getVolumenMaximoM3() > 0 && container.getVolumenM3() != null) {
            return (container.getVolumenM3() / container.getVolumenMaximoM3()) * 100;
        }
        return 0.0;
    }
}
