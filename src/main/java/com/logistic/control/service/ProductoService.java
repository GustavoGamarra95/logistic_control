package com.logistic.control.service;

import com.logistic.control.dto.request.ProductoRequest;
import com.logistic.control.dto.response.ProductoResponse;
import com.logistic.control.entity.Producto;
import com.logistic.control.exception.DuplicateResourceException;
import com.logistic.control.exception.ResourceNotFoundException;
import com.logistic.control.exception.BusinessException;
import com.logistic.control.repository.ProductoRepository;
import com.logistic.control.security.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestión de Productos/Mercadería
 * Adaptado a los campos reales: codigo, descripcion, pesoKg, volumenM3, valorUnitario, etc.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final InputSanitizer inputSanitizer;

    /**
     * Listar todos los productos con paginación
     */
    public Page<ProductoResponse> listarProductos(Pageable pageable) {
        log.debug("Listando productos - página: {}", pageable.getPageNumber());
        return productoRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtener producto por ID
     */
    public ProductoResponse obtenerProducto(Long id) {
        log.debug("Obteniendo producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        return toResponse(producto);
    }

    /**
     * Buscar producto por código
     */
    public ProductoResponse buscarPorCodigo(String codigo) {
        String codigoSanitized = inputSanitizer.sanitize(codigo);
        log.debug("Buscando producto por código: {}", codigoSanitized);
        
        Producto producto = productoRepository.findByCodigo(codigoSanitized)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "código", codigoSanitized));
        return toResponse(producto);
    }

    /**
     * Crear nuevo producto
     */
    @Transactional
    public ProductoResponse crearProducto(ProductoRequest request) {
        log.info("Creando nuevo producto: {}", request.getCodigo());
        
        // Validar duplicados
        if (productoRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new DuplicateResourceException("Producto", "código", request.getCodigo());
        }
        
        // Validar datos de negocio
        validarDatosProducto(request);
        
        // Crear entidad
        Producto producto = Producto.builder()
                .codigo(inputSanitizer.sanitize(request.getCodigo()))
                .descripcion(inputSanitizer.sanitize(request.getDescripcion()))
                .descripcionDetallada(inputSanitizer.sanitize(request.getDescripcionDetallada()))
                .codigoNcm(request.getCodigoNcm())
                .codigoArancel(request.getCodigoArancel())
                .pesoKg(request.getPesoKg())
                .volumenM3(request.getVolumenM3())
                .unidadMedida(inputSanitizer.sanitize(request.getUnidadMedida()))
                .cantidadPorUnidad(request.getCantidadPorUnidad())
                .paisOrigen(request.getPaisOrigen())
                .valorUnitario(request.getValorUnitario())
                .moneda(request.getMoneda())
                .tasaIva(request.getTasaIva())
                .precioVenta(request.getPrecioVenta())
                .esPeligroso(request.getEsPeligroso() != null ? request.getEsPeligroso() : false)
                .esPerecedero(request.getEsPerecedero() != null ? request.getEsPerecedero() : false)
                .esFragil(request.getEsFragil() != null ? request.getEsFragil() : false)
                .requiereRefrigeracion(request.getRequiereRefrigeracion() != null ? 
                        request.getRequiereRefrigeracion() : false)
                .temperaturaMin(request.getTemperaturaMin())
                .temperaturaMax(request.getTemperaturaMax())
                .observaciones(request.getObservaciones())
                .build();
        
        Producto saved = productoRepository.save(producto);
        log.info("Producto creado exitosamente con ID: {}", saved.getId());
        
        return toResponse(saved);
    }

    /**
     * Actualizar producto existente
     */
    @Transactional
    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) {
        log.info("Actualizando producto ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        // Validar duplicados (excepto el mismo producto)
        productoRepository.findByCodigo(request.getCodigo()).ifPresent(p -> {
            if (!p.getId().equals(id)) {
                throw new DuplicateResourceException("Producto", "código", request.getCodigo());
            }
        });
        
        // Validar datos de negocio
        validarDatosProducto(request);
        
        // Actualizar campos
        producto.setCodigo(inputSanitizer.sanitize(request.getCodigo()));
        producto.setDescripcion(inputSanitizer.sanitize(request.getDescripcion()));
        producto.setDescripcionDetallada(inputSanitizer.sanitize(request.getDescripcionDetallada()));
        producto.setCodigoNcm(request.getCodigoNcm());
        producto.setCodigoArancel(request.getCodigoArancel());
        producto.setPesoKg(request.getPesoKg());
        producto.setVolumenM3(request.getVolumenM3());
        producto.setUnidadMedida(inputSanitizer.sanitize(request.getUnidadMedida()));
        producto.setCantidadPorUnidad(request.getCantidadPorUnidad());
        producto.setPaisOrigen(request.getPaisOrigen());
        producto.setValorUnitario(request.getValorUnitario());
        producto.setMoneda(request.getMoneda());
        producto.setTasaIva(request.getTasaIva());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setTemperaturaMin(request.getTemperaturaMin());
        producto.setTemperaturaMax(request.getTemperaturaMax());
        producto.setObservaciones(request.getObservaciones());
        
        if (request.getEsPeligroso() != null) {
            producto.setEsPeligroso(request.getEsPeligroso());
        }
        if (request.getEsPerecedero() != null) {
            producto.setEsPerecedero(request.getEsPerecedero());
        }
        if (request.getEsFragil() != null) {
            producto.setEsFragil(request.getEsFragil());
        }
        if (request.getRequiereRefrigeracion() != null) {
            producto.setRequiereRefrigeracion(request.getRequiereRefrigeracion());
        }
        
        Producto updated = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente: {}", updated.getId());
        
        return toResponse(updated);
    }

    /**
     * Eliminar producto (marcar en observaciones)
     */
    @Transactional
    public void eliminarProducto(Long id) {
        log.info("Marcando producto como eliminado ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        String obs = producto.getObservaciones() != null ? producto.getObservaciones() : "";
        producto.setObservaciones(obs + " [ELIMINADO]");
        productoRepository.save(producto);
        
        log.info("Producto marcado como eliminado: {}", id);
    }

    /**
     * Validar datos de negocio del producto
     */
    private void validarDatosProducto(ProductoRequest request) {
        // Validar peso (requerido)
        if (request.getPesoKg() == null || request.getPesoKg() <= 0) {
            throw new BusinessException("El peso debe ser mayor a cero");
        }
        
        // Validar volumen
        if (request.getVolumenM3() != null && request.getVolumenM3() < 0) {
            throw new BusinessException("El volumen no puede ser negativo");
        }
        
        // Validar valor unitario
        if (request.getValorUnitario() != null && request.getValorUnitario() < 0) {
            throw new BusinessException("El valor unitario no puede ser negativo");
        }
        
        // Validar temperaturas
        if (request.getTemperaturaMin() != null && request.getTemperaturaMax() != null &&
            request.getTemperaturaMin() > request.getTemperaturaMax()) {
            throw new BusinessException("La temperatura mínima no puede ser mayor a la máxima");
        }
    }

    /**
     * Convertir entidad a DTO de respuesta
     */
    private ProductoResponse toResponse(Producto producto) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .codigo(producto.getCodigo())
                .descripcion(producto.getDescripcion())
                .descripcionDetallada(producto.getDescripcionDetallada())
                .codigoNcm(producto.getCodigoNcm())
                .codigoArancel(producto.getCodigoArancel())
                .pesoKg(producto.getPesoKg())
                .volumenM3(producto.getVolumenM3())
                .unidadMedida(producto.getUnidadMedida())
                .cantidadPorUnidad(producto.getCantidadPorUnidad())
                .paisOrigen(producto.getPaisOrigen())
                .valorUnitario(producto.getValorUnitario())
                .moneda(producto.getMoneda())
                .tasaIva(producto.getTasaIva())
                .precioVenta(producto.getPrecioVenta())
                .esPeligroso(producto.getEsPeligroso())
                .esPerecedero(producto.getEsPerecedero())
                .esFragil(producto.getEsFragil())
                .requiereRefrigeracion(producto.getRequiereRefrigeracion())
                .temperaturaMin(producto.getTemperaturaMin())
                .temperaturaMax(producto.getTemperaturaMax())
                .observaciones(producto.getObservaciones())
                .createdAt(producto.getCreatedAt())
                .updatedAt(producto.getUpdatedAt())
                .build();
    }

    /**
     * Buscar productos por código NCM
     */
    public Page<ProductoResponse> buscarPorCodigoNcm(String ncm, Pageable pageable) {
        log.debug("Buscando productos por código NCM: {}", ncm);
        String ncmSanitized = inputSanitizer.sanitize(ncm);

        return productoRepository.findByCodigoNcm(ncmSanitized, pageable)
                .map(this::toResponse);
    }

    /**
     * Listar productos peligrosos
     */
    public Page<ProductoResponse> listarProductosPeligrosos(Pageable pageable) {
        log.debug("Listando productos peligrosos");
        return productoRepository.findByEsPeligrosoTrue(pageable)
                .map(this::toResponse);
    }

    /**
     * Listar productos que requieren refrigeración
     */
    public Page<ProductoResponse> listarProductosRefrigerados(Pageable pageable) {
        log.debug("Listando productos que requieren refrigeración");
        return productoRepository.findByRequiereRefrigeracionTrue(pageable)
                .map(this::toResponse);
    }

    /**
     * Buscar productos por nombre/descripción
     */
    public Page<ProductoResponse> buscarPorNombre(String nombre, Pageable pageable) {
        log.debug("Buscando productos por nombre: {}", nombre);
        String nombreSanitized = inputSanitizer.sanitize(nombre);

        return productoRepository.searchByNombre(nombreSanitized, pageable)
                .map(this::toResponse);
    }
}
