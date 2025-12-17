package com.logistic.control.service;

import com.logistic.control.dto.request.ProveedorRequest;
import com.logistic.control.dto.response.ProveedorResponse;
import com.logistic.control.entity.Proveedor;
import com.logistic.control.exception.DuplicateResourceException;
import com.logistic.control.exception.ResourceNotFoundException;
import com.logistic.control.exception.BusinessException;
import com.logistic.control.repository.ProveedorRepository;
import com.logistic.control.security.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestión de Proveedores
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final InputSanitizer inputSanitizer;

    /**
     * Listar todos los proveedores con paginación
     */
    public Page<ProveedorResponse> listarProveedores(Pageable pageable) {
        log.debug("Listando proveedores - página: {}", pageable.getPageNumber());
        return proveedorRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtener proveedor por ID
     */
    public ProveedorResponse obtenerProveedor(Long id) {
        log.debug("Obteniendo proveedor con ID: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));
        return toResponse(proveedor);
    }

    /**
     * Buscar proveedor por RUC
     */
    public ProveedorResponse buscarPorRuc(String ruc) {
        String rucSanitized = inputSanitizer.sanitize(ruc);
        log.debug("Buscando proveedor por RUC: {}", rucSanitized);

        Proveedor proveedor = proveedorRepository.findByRuc(rucSanitized)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "RUC", rucSanitized));
        return toResponse(proveedor);
    }

    /**
     * Listar proveedores por tipo
     */
    public List<ProveedorResponse> listarPorTipo(com.logistic.control.enums.TipoProveedor tipo) {
        log.debug("Listando proveedores por tipo: {}", tipo);
        return proveedorRepository.findByTipo(tipo).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Crear nuevo proveedor
     */
    @Transactional
    public ProveedorResponse crearProveedor(ProveedorRequest request) {
        log.info("Creando nuevo proveedor: {}", request.getRazonSocial());
        
        // Validar duplicados
        if (proveedorRepository.findByRuc(request.getRuc()).isPresent()) {
            throw new DuplicateResourceException("Proveedor", "RUC", request.getRuc());
        }
        
        // Validar datos
        validarDatosProveedor(request);
        
        // Crear entidad usando campos que existen en Proveedor entity
        Proveedor proveedor = Proveedor.builder()
                .nombre(inputSanitizer.sanitize(request.getNombre()))
                .razonSocial(inputSanitizer.sanitize(request.getRazonSocial()))
                .ruc(inputSanitizer.sanitize(request.getRuc()))
                .tipo(request.getTipo())
                .direccion(inputSanitizer.sanitize(request.getDireccion()))
                .ciudad(inputSanitizer.sanitize(request.getCiudad()))
                .pais(inputSanitizer.sanitize(request.getPais()))
                .contacto(inputSanitizer.sanitize(request.getContacto()))
                .email(inputSanitizer.sanitizeForXss(request.getEmail()))
                .telefono(request.getTelefono())
                .costoServicio(request.getCostoServicio())
                .moneda(request.getMoneda() != null ? request.getMoneda() : "PYG")
                .plazoPagoDias(request.getPlazoPagoDias())
                .cuentaBancaria(request.getCuentaBancaria())
                .banco(request.getBanco())
                .observaciones(request.getObservaciones())
                .build();
        
        Proveedor saved = proveedorRepository.save(proveedor);
        log.info("Proveedor creado exitosamente con ID: {}", saved.getId());
        
        return toResponse(saved);
    }

    /**
     * Actualizar proveedor existente
     */
    @Transactional
    public ProveedorResponse actualizarProveedor(Long id, ProveedorRequest request) {
        log.info("Actualizando proveedor ID: {}", id);
        
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));
        
        // Validar duplicados (excepto el mismo proveedor)
        validarDuplicadosExcepto(id, request.getRuc());
        validarDatosProveedor(request);
        
        // Actualizar campos usando campos que existen en Proveedor entity
        proveedor.setNombre(inputSanitizer.sanitize(request.getNombre()));
        proveedor.setRazonSocial(inputSanitizer.sanitize(request.getRazonSocial()));
        proveedor.setRuc(inputSanitizer.sanitize(request.getRuc()));
        proveedor.setTipo(request.getTipo());
        proveedor.setDireccion(inputSanitizer.sanitize(request.getDireccion()));
        proveedor.setCiudad(inputSanitizer.sanitize(request.getCiudad()));
        proveedor.setPais(inputSanitizer.sanitize(request.getPais()));
        proveedor.setContacto(inputSanitizer.sanitize(request.getContacto()));
        proveedor.setEmail(inputSanitizer.sanitizeForXss(request.getEmail()));
        proveedor.setTelefono(request.getTelefono());
        proveedor.setCostoServicio(request.getCostoServicio());
        proveedor.setMoneda(request.getMoneda());
        proveedor.setPlazoPagoDias(request.getPlazoPagoDias());
        proveedor.setCuentaBancaria(request.getCuentaBancaria());
        proveedor.setBanco(request.getBanco());
        proveedor.setObservaciones(request.getObservaciones());
        
        Proveedor updated = proveedorRepository.save(proveedor);
        log.info("Proveedor actualizado exitosamente: {}", updated.getId());
        
        return toResponse(updated);
    }

    /**
     * Eliminar proveedor (soft delete)
     */
    @Transactional
    public void eliminarProveedor(Long id) {
        log.info("Eliminando proveedor ID: {}", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));

        // Simplemente eliminar el registro
        proveedorRepository.delete(proveedor);

        log.info("Proveedor eliminado exitosamente: {}", id);
    }

    /**
     * Validar duplicados excepto el mismo proveedor
     */
    private void validarDuplicadosExcepto(Long id, String ruc) {
        proveedorRepository.findByRuc(ruc).ifPresent(p -> {
            if (!p.getId().equals(id)) {
                throw new DuplicateResourceException("Proveedor", "RUC", ruc);
            }
        });
    }

    /**
     * Validar datos de negocio
     */
    private void validarDatosProveedor(ProveedorRequest request) {
        if (!inputSanitizer.isValidRuc(request.getRuc())) {
            throw new BusinessException("Formato de RUC inválido");
        }
        
        if (!inputSanitizer.isValidEmail(request.getEmail())) {
            throw new BusinessException("Formato de email inválido");
        }
        
        if (request.getPlazoPagoDias() != null && request.getPlazoPagoDias() < 0) {
            throw new BusinessException("El plazo de pago no puede ser negativo");
        }
    }

    /**
     * Convertir entidad a DTO de respuesta
     */
    private ProveedorResponse toResponse(Proveedor proveedor) {
        return ProveedorResponse.builder()
                .id(proveedor.getId())
                .nombre(proveedor.getNombre())
                .razonSocial(proveedor.getRazonSocial())
                .ruc(proveedor.getRuc())
                .tipo(proveedor.getTipo())
                .direccion(proveedor.getDireccion())
                .ciudad(proveedor.getCiudad())
                .pais(proveedor.getPais())
                .contacto(proveedor.getContacto())
                .email(proveedor.getEmail())
                .telefono(proveedor.getTelefono())
                .costoServicio(proveedor.getCostoServicio())
                .moneda(proveedor.getMoneda())
                .plazoPagoDias(proveedor.getPlazoPagoDias())
                .cuentaBancaria(proveedor.getCuentaBancaria())
                .banco(proveedor.getBanco())
                .calificacion(proveedor.getCalificacion())
                .observaciones(proveedor.getObservaciones())
                .createdAt(proveedor.getCreatedAt())
                .updatedAt(proveedor.getUpdatedAt())
                .build();
    }
}
