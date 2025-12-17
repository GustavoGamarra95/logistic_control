package com.logistic.control.service;

import com.logistic.control.dto.request.ClienteRequest;
import com.logistic.control.dto.response.ClienteResponse;
import com.logistic.control.entity.Cliente;
import com.logistic.control.exception.DuplicateResourceException;
import com.logistic.control.exception.ResourceNotFoundException;
import com.logistic.control.exception.BusinessException;
import com.logistic.control.repository.ClienteRepository;
import com.logistic.control.security.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Servicio para gestión de Clientes
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final InputSanitizer inputSanitizer;

    /**
     * Listar todos los clientes con paginación
     */
    public Page<ClienteResponse> listarClientes(Pageable pageable) {
        log.debug("Listando clientes - página: {}", pageable.getPageNumber());
        return clienteRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtener cliente por ID
     */
    public ClienteResponse obtenerCliente(Long id) {
        log.debug("Obteniendo cliente con ID: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return toResponse(cliente);
    }

    /**
     * Buscar cliente por RUC
     */
    public ClienteResponse buscarPorRuc(String ruc) {
        String rucSanitized = inputSanitizer.sanitize(ruc);
        log.debug("Buscando cliente por RUC: {}", rucSanitized);
        
        Cliente cliente = clienteRepository.findByRuc(rucSanitized)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "RUC", rucSanitized));
        return toResponse(cliente);
    }

    /**
     * Buscar cliente por email
     */
    public ClienteResponse buscarPorEmail(String email) {
        String emailSanitized = inputSanitizer.sanitizeForXss(email);
        log.debug("Buscando cliente por email: {}", emailSanitized);
        
        Cliente cliente = clienteRepository.findByEmail(emailSanitized)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "email", emailSanitized));
        return toResponse(cliente);
    }

    /**
     * Crear nuevo cliente
     */
    @Transactional
    public ClienteResponse crearCliente(ClienteRequest request) {
        log.info("Creando nuevo cliente: {}", request.getRazonSocial());
        
        // Validar datos de negocio (normaliza ruc/dv)
        validarDatosCliente(request);

        // Validar duplicados
        validarDuplicados(request.getRuc(), request.getEmail());
        
        // Crear entidad
        Cliente cliente = Cliente.builder()
                .razonSocial(inputSanitizer.sanitize(request.getRazonSocial()))
                .nombreFantasia(inputSanitizer.sanitize(request.getNombreFantasia()))
                .ruc(inputSanitizer.sanitize(request.getRuc()))
                .dv(request.getDv())
                .direccion(inputSanitizer.sanitize(request.getDireccion()))
                .ciudad(inputSanitizer.sanitize(request.getCiudad()))
                .pais(inputSanitizer.sanitize(request.getPais()))
                .contacto(inputSanitizer.sanitize(request.getContacto()))
                .email(inputSanitizer.sanitizeForXss(request.getEmail()))
                .telefono(request.getTelefono())
                .celular(request.getCelular())
                .tipoServicio(request.getTipoServicio())
                .creditoLimite(request.getCreditoLimite())
                .creditoDisponible(request.getCreditoLimite()) // Inicialmente igual al límite
                .esFacturadorElectronico(request.getEsFacturadorElectronico() != null ? 
                        request.getEsFacturadorElectronico() : false)
                .observaciones(inputSanitizer.sanitize(request.getObservaciones()))
                .build();
        
        Cliente saved = clienteRepository.save(cliente);
        log.info("Cliente creado exitosamente con ID: {}", saved.getId());
        
        return toResponse(saved);
    }

    /**
     * Actualizar cliente existente
     */
    @Transactional
    public ClienteResponse actualizarCliente(Long id, ClienteRequest request) {
        log.info("Actualizando cliente ID: {}", id);
        
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        
        // Validar datos de negocio (normaliza ruc/dv)
        validarDatosCliente(request);

        // Validar duplicados (excepto el mismo cliente)
        validarDuplicadosExcepto(id, request.getRuc(), request.getEmail());
        
        // Actualizar campos
        cliente.setRazonSocial(inputSanitizer.sanitize(request.getRazonSocial()));
        cliente.setNombreFantasia(inputSanitizer.sanitize(request.getNombreFantasia()));
        cliente.setRuc(inputSanitizer.sanitize(request.getRuc()));
        cliente.setDv(request.getDv());
        cliente.setDireccion(inputSanitizer.sanitize(request.getDireccion()));
        cliente.setCiudad(inputSanitizer.sanitize(request.getCiudad()));
        cliente.setPais(inputSanitizer.sanitize(request.getPais()));
        cliente.setContacto(inputSanitizer.sanitize(request.getContacto()));
        cliente.setEmail(inputSanitizer.sanitizeForXss(request.getEmail()));
        cliente.setTelefono(request.getTelefono());
        cliente.setCelular(request.getCelular());
        cliente.setTipoServicio(request.getTipoServicio());
        cliente.setObservaciones(inputSanitizer.sanitize(request.getObservaciones()));
        
        // Si cambia el límite de crédito, ajustar el disponible proporcionalmente
        if (request.getCreditoLimite() != null && 
            !request.getCreditoLimite().equals(cliente.getCreditoLimite())) {
            
            Double limiteAnterior = cliente.getCreditoLimite();
            Double dispAnterior = cliente.getCreditoDisponible();
            Double nuevoLimite = request.getCreditoLimite();

            // Manejar valores nulos: si no existe disponible, asumir disponible igual al límite anterior
            if (dispAnterior == null) {
                dispAnterior = (limiteAnterior != null) ? limiteAnterior : nuevoLimite;
            }

            double nuevoDisponible;
            if (limiteAnterior != null && limiteAnterior > 0) {
                double proporcion = dispAnterior / limiteAnterior;
                nuevoDisponible = nuevoLimite * proporcion;
            } else {
                // Si no hay límite anterior definido, asignar todo el nuevo límite
                nuevoDisponible = nuevoLimite;
            }

            // Evitar que quede negativo o superior al nuevo límite
            if (nuevoDisponible < 0) {
                log.warn("Crédito disponible calculado negativo ({}). Se ajusta a 0. Cliente: {}", nuevoDisponible, cliente.getId());
                nuevoDisponible = 0.0;
            }

            if (nuevoDisponible > nuevoLimite) {
                log.warn("Crédito disponible calculado mayor al nuevo límite ({} > {}). Se ajusta al nuevo límite. Cliente: {}",
                        nuevoDisponible, nuevoLimite, cliente.getId());
                nuevoDisponible = nuevoLimite;
            }

            cliente.setCreditoDisponible(nuevoDisponible);
            cliente.setCreditoLimite(nuevoLimite);
        }
        
        if (request.getEsFacturadorElectronico() != null) {
            cliente.setEsFacturadorElectronico(request.getEsFacturadorElectronico());
        }
        
        Cliente updated = clienteRepository.save(cliente);
        log.info("Cliente actualizado exitosamente: {}", updated.getId());
        
        return toResponse(updated);
    }

    /**
     * Eliminar cliente
     */
    @Transactional
    public void eliminarCliente(Long id) {
        log.info("Eliminando cliente ID: {}", id);
        
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        
        // Validar que no tenga pedidos o facturas pendientes
        if (!cliente.getPedidos().isEmpty()) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene pedidos asociados");
        }
        
        if (!cliente.getFacturas().isEmpty()) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene facturas asociadas");
        }
        
        clienteRepository.delete(cliente);
        log.info("Cliente eliminado exitosamente: {}", id);
    }

    /**
     * Actualizar crédito disponible (retorna void para uso interno)
     */
    @Transactional
    public void actualizarCreditoDisponible(Long clienteId, BigDecimal monto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId));

        Double nuevoDisponible = cliente.getCreditoDisponible() + monto.doubleValue();

        if (nuevoDisponible < 0) {
            throw new BusinessException("El crédito disponible no puede ser negativo");
        }

        if (nuevoDisponible > cliente.getCreditoLimite()) {
            throw new BusinessException("El crédito disponible no puede exceder el límite");
        }

        cliente.setCreditoDisponible(nuevoDisponible);
        clienteRepository.save(cliente);

        log.debug("Crédito actualizado para cliente {}: {}", clienteId, nuevoDisponible);
    }

    /**
     * Actualizar crédito (retorna respuesta para API)
     */
    @Transactional
    public ClienteResponse actualizarCredito(Long id, Double monto) {
        log.info("Actualizando crédito del cliente ID: {} con monto: {}", id, monto);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // Usar la lógica del entity que valida y evita saldo negativo
        cliente.updateCreditoDisponible(monto);
        Cliente updated = clienteRepository.save(cliente);

        log.info("Crédito actualizado exitosamente. Nuevo disponible: {}", updated.getCreditoDisponible());
        return toResponse(updated);
    }

    /**
     * Verificar RUC en SIFEN
     */
    @Transactional
    public ClienteResponse verificarRucEnSifen(Long id) {
        log.info("Verificando RUC en SIFEN para cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // TODO: Implementar integración real con SIFEN
        // Por ahora solo actualizamos el estado a ACTIVO
        cliente.setEstadoRuc("ACTIVO");
        Cliente updated = clienteRepository.save(cliente);

        log.info("RUC verificado (simulado) para cliente: {}", cliente.getRuc());
        return toResponse(updated);
    }

    /**
     * Validar duplicados al crear
     */
    private void validarDuplicados(String ruc, String email) {
        if (clienteRepository.findByRuc(ruc).isPresent()) {
            throw new DuplicateResourceException("Cliente", "RUC", ruc);
        }
        
        if (clienteRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Cliente", "email", email);
        }
    }

    /**
     * Validar duplicados al actualizar (excepto el mismo cliente)
     */
    private void validarDuplicadosExcepto(Long id, String ruc, String email) {
        clienteRepository.findByRuc(ruc).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new DuplicateResourceException("Cliente", "RUC", ruc);
            }
        });
        
        clienteRepository.findByEmail(email).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new DuplicateResourceException("Cliente", "email", email);
            }
        });
    }

    /**
     * Validar datos de negocio del cliente
     */
    private void validarDatosCliente(ClienteRequest request) {
        // Normalizar RUC y DV: aceptar ruc con DV (12345678-9) o ruc y dv separados
        String rucRaw = request.getRuc();
        String dvRaw = request.getDv();

        if (rucRaw != null && rucRaw.contains("-")) {
            String[] parts = rucRaw.split("-", 2);
            rucRaw = parts[0];
            // Si dv no fue provisto por separado, tomar de ruc
            if (dvRaw == null || dvRaw.isEmpty()) {
                dvRaw = parts.length > 1 ? parts[1] : null;
            }
        }

        // Actualizar request (no es inmutable) para que el resto del flujo use los valores compuestos
        request.setRuc(rucRaw);
        request.setDv(dvRaw);

        // Validar RUC con formato paraguayo compuesto (se espera ruc + dv separado)
        String composed = rucRaw;
        if (dvRaw != null && !dvRaw.isEmpty()) {
            composed = rucRaw + "-" + dvRaw;
        }

        if (!inputSanitizer.isValidRuc(composed)) {
            throw new BusinessException("Formato de RUC inválido. Debe ser: 12345678-9");
        }

        // Validar email
        if (!inputSanitizer.isValidEmail(request.getEmail())) {
            throw new BusinessException("Formato de email inválido");
        }

        // Validar límite de crédito
        if (request.getCreditoLimite() != null && 
            request.getCreditoLimite() < 0) {
            throw new BusinessException("El límite de crédito no puede ser negativo");
        }
    }

    /**
     * Convertir entidad a DTO de respuesta
     */
    private ClienteResponse toResponse(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .razonSocial(cliente.getRazonSocial())
                .nombreFantasia(cliente.getNombreFantasia())
                .ruc(cliente.getRuc())
                .dv(cliente.getDv())
                .direccion(cliente.getDireccion())
                .ciudad(cliente.getCiudad())
                .pais(cliente.getPais())
                .contacto(cliente.getContacto())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .celular(cliente.getCelular())
                .tipoServicio(cliente.getTipoServicio())
                .creditoLimite(cliente.getCreditoLimite())
                .creditoDisponible(cliente.getCreditoDisponible())
                .esFacturadorElectronico(cliente.getEsFacturadorElectronico())
                .estadoRuc(cliente.getEstadoRuc())
                .observaciones(cliente.getObservaciones())
                .createdAt(cliente.getCreatedAt())
                .updatedAt(cliente.getUpdatedAt())
                .build();
    }
}
