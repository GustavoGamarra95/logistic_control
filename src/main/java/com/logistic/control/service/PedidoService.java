package com.logistic.control.service;

import com.logistic.control.dto.request.PedidoRequest;
import com.logistic.control.dto.response.PedidoResponse;
import com.logistic.control.entity.*;
import com.logistic.control.enums.EstadoPedido;
import com.logistic.control.exception.ResourceNotFoundException;
import com.logistic.control.exception.BusinessException;
import com.logistic.control.exception.InvalidStateException;
import com.logistic.control.repository.PedidoRepository;
import com.logistic.control.repository.ClienteRepository;
import com.logistic.control.security.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio simplificado para gestión de Pedidos logísticos
 * Modelo adaptado al flujo real del proyecto
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final InputSanitizer inputSanitizer;

    /**
     * Listar todos los pedidos con paginación
     */
    public Page<PedidoResponse> listarPedidos(Pageable pageable) {
        log.debug("Listando pedidos - página: {}", pageable.getPageNumber());
        return pedidoRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtener pedido por ID
     */
    public PedidoResponse obtenerPedido(Long id) {
        log.debug("Obteniendo pedido con ID: {}", id);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        return toResponse(pedido);
    }

    /**
     * Buscar pedidos por cliente
     */
    public Page<PedidoResponse> buscarPorCliente(Long clienteId, Pageable pageable) {
        log.debug("Buscando pedidos del cliente: {}", clienteId);
        return pedidoRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Buscar pedidos por estado
     */
    public Page<PedidoResponse> listarPorEstado(EstadoPedido estado, Pageable pageable) {
        log.debug("Listando pedidos por estado: {}", estado);
        return pedidoRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Crear nuevo pedido
     */
    @Transactional
    public PedidoResponse crearPedido(PedidoRequest request) {
        log.info("Creando nuevo pedido para cliente: {}", request.getClienteId());
        
        // Validar cliente
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClienteId()));
        
        // Crear pedido
        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .fechaRegistro(LocalDateTime.now())
                .tipoCarga(request.getTipoCarga())
                .paisOrigen(inputSanitizer.sanitize(request.getPaisOrigen()))
                .paisDestino(inputSanitizer.sanitize(request.getPaisDestino()))
                .ciudadOrigen(inputSanitizer.sanitize(request.getCiudadOrigen()))
                .ciudadDestino(inputSanitizer.sanitize(request.getCiudadDestino()))
                .descripcionMercaderia(inputSanitizer.sanitize(request.getDescripcionMercaderia()))
                .numeroContenedorGuia(request.getNumeroContenedorGuia())
                .estado(EstadoPedido.REGISTRADO)
                .codigoTracking(generarCodigoTracking())
                .fechaEstimadaLlegada(request.getFechaEstimadaLlegada())
                .pesoTotalKg(request.getPesoTotalKg())
                .volumenTotalM3(request.getVolumenTotalM3())
                .valorDeclarado(request.getValorDeclarado())
                .moneda(request.getMoneda())
                .numeroBlAwb(request.getNumeroBlAwb())
                .puertoEmbarque(request.getPuertoEmbarque())
                .puertoDestino(request.getPuertoDestino())
                .empresaTransporte(request.getEmpresaTransporte())
                .requiereSeguro(request.getRequiereSeguro() != null ? request.getRequiereSeguro() : false)
                .valorSeguro(request.getValorSeguro())
                .observaciones(inputSanitizer.sanitize(request.getObservaciones()))
                .build();
        
        Pedido saved = pedidoRepository.save(pedido);
        log.info("Pedido creado exitosamente con ID: {} y tracking: {}", saved.getId(), saved.getCodigoTracking());
        
        return toResponse(saved);
    }

    /**
     * Actualizar pedido existente
     */
    @Transactional
    public PedidoResponse actualizarPedido(Long id, PedidoRequest request) {
        log.info("Actualizando pedido ID: {}", id);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        
        // No se puede modificar un pedido entregado o cancelado
        if (pedido.getEstado() == EstadoPedido.ENTREGADO || 
            pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new BusinessException("No se puede modificar un pedido " + pedido.getEstado());
        }
        
        // Actualizar campos
        pedido.setTipoCarga(request.getTipoCarga());
        pedido.setPaisOrigen(inputSanitizer.sanitize(request.getPaisOrigen()));
        pedido.setPaisDestino(inputSanitizer.sanitize(request.getPaisDestino()));
        pedido.setCiudadOrigen(inputSanitizer.sanitize(request.getCiudadOrigen()));
        pedido.setCiudadDestino(inputSanitizer.sanitize(request.getCiudadDestino()));
        pedido.setDescripcionMercaderia(inputSanitizer.sanitize(request.getDescripcionMercaderia()));
        pedido.setNumeroContenedorGuia(request.getNumeroContenedorGuia());
        pedido.setFechaEstimadaLlegada(request.getFechaEstimadaLlegada());
        pedido.setPesoTotalKg(request.getPesoTotalKg());
        pedido.setVolumenTotalM3(request.getVolumenTotalM3());
        pedido.setValorDeclarado(request.getValorDeclarado());
        pedido.setMoneda(request.getMoneda());
        pedido.setNumeroBlAwb(request.getNumeroBlAwb());
        pedido.setPuertoEmbarque(request.getPuertoEmbarque());
        pedido.setPuertoDestino(request.getPuertoDestino());
        pedido.setEmpresaTransporte(request.getEmpresaTransporte());
        
        if (request.getRequiereSeguro() != null) {
            pedido.setRequiereSeguro(request.getRequiereSeguro());
        }
        pedido.setValorSeguro(request.getValorSeguro());
        pedido.setObservaciones(inputSanitizer.sanitize(request.getObservaciones()));
        
        Pedido updated = pedidoRepository.save(pedido);
        log.info("Pedido actualizado exitosamente: {}", updated.getId());
        
        return toResponse(updated);
    }

    /**
     * Cancelar pedido
     */
    @Transactional
    public void cancelarPedido(Long id, String motivo) {
        log.info("Cancelando pedido ID: {}", id);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        
        // Validar estado
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new BusinessException("No se puede cancelar un pedido ya entregado");
        }
        
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new BusinessException("El pedido ya está cancelado");
        }
        
        // Cancelar
        pedido.setEstado(EstadoPedido.CANCELADO);
        String obs = pedido.getObservaciones() != null ? pedido.getObservaciones() : "";
        pedido.setObservaciones(obs + " [CANCELADO: " + inputSanitizer.sanitize(motivo) + "]");
        
        pedidoRepository.save(pedido);
        log.info("Pedido cancelado exitosamente: {}", id);
    }

    /**
     * Cambiar estado del pedido
     */
    @Transactional
    public PedidoResponse cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        log.info("Cambiando estado del pedido {} a {}", id, nuevoEstado);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        
        EstadoPedido estadoActual = pedido.getEstado();
        
        // Validar transición de estados
        validarTransicionEstado(estadoActual, nuevoEstado);
        
        pedido.setEstado(nuevoEstado);
        
        // Actualizar fechas según estado
        if (nuevoEstado == EstadoPedido.ENTREGADO) {
            pedido.setFechaLlegadaReal(pedido.getFechaEstimadaLlegada());
        }
        
        Pedido updated = pedidoRepository.save(pedido);
        log.info("Estado del pedido actualizado exitosamente");
        
        return toResponse(updated);
    }

    /**
     * Validar transición de estados
     */
    private void validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido nuevoEstado) {
        // Pedidos cancelados no pueden cambiar de estado
        if (estadoActual == EstadoPedido.CANCELADO) {
            throw new InvalidStateException("No se puede cambiar el estado de un pedido cancelado");
        }
        
        // Pedidos entregados solo pueden marcarse como devueltos
        if (estadoActual == EstadoPedido.ENTREGADO && nuevoEstado != EstadoPedido.DEVUELTO) {
            throw new InvalidStateException("Un pedido entregado solo puede marcarse como DEVUELTO");
        }
        
        // No se puede volver a estados anteriores (excepto DEVUELTO)
        if (nuevoEstado != EstadoPedido.DEVUELTO && nuevoEstado != EstadoPedido.CANCELADO) {
            int ordenActual = getOrdenEstado(estadoActual);
            int ordenNuevo = getOrdenEstado(nuevoEstado);
            
            if (ordenNuevo < ordenActual) {
                throw new InvalidStateException(
                    String.format("No se puede retroceder de %s a %s", estadoActual, nuevoEstado));
            }
        }
    }

    /**
     * Obtener orden del estado para validaciones
     */
    private int getOrdenEstado(EstadoPedido estado) {
        return switch (estado) {
            case REGISTRADO -> 1;
            case EN_TRANSITO -> 2;
            case RECIBIDO -> 3;
            case EN_ADUANA -> 4;
            case LIBERADO -> 5;
            case EN_DEPOSITO -> 6;
            case EN_REPARTO -> 7;
            case ENTREGADO -> 8;
            case CANCELADO -> 0;
            case DEVUELTO -> 0;
        };
    }

    /**
     * Generar código de tracking único
     */
    private String generarCodigoTracking() {
        return "PED-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Convertir entidad a DTO de respuesta
     */
    private PedidoResponse toResponse(Pedido pedido) {
        return PedidoResponse.builder()
                .id(pedido.getId())
                .clienteId(pedido.getCliente().getId())
                .clienteNombre(pedido.getCliente().getRazonSocial())
                .fechaRegistro(pedido.getFechaRegistro())
                .tipoCarga(pedido.getTipoCarga())
                .paisOrigen(pedido.getPaisOrigen())
                .paisDestino(pedido.getPaisDestino())
                .ciudadOrigen(pedido.getCiudadOrigen())
                .ciudadDestino(pedido.getCiudadDestino())
                .descripcionMercaderia(pedido.getDescripcionMercaderia())
                .numeroContenedorGuia(pedido.getNumeroContenedorGuia())
                .estado(pedido.getEstado())
                .codigoTracking(pedido.getCodigoTracking())
                .fechaEstimadaLlegada(pedido.getFechaEstimadaLlegada())
                .fechaLlegadaReal(pedido.getFechaLlegadaReal())
                .pesoTotalKg(pedido.getPesoTotalKg())
                .volumenTotalM3(pedido.getVolumenTotalM3())
                .valorDeclarado(pedido.getValorDeclarado())
                .moneda(pedido.getMoneda())
                .numeroBlAwb(pedido.getNumeroBlAwb())
                .puertoEmbarque(pedido.getPuertoEmbarque())
                .puertoDestino(pedido.getPuertoDestino())
                .empresaTransporte(pedido.getEmpresaTransporte())
                .requiereSeguro(pedido.getRequiereSeguro())
                .valorSeguro(pedido.getValorSeguro())
                .observaciones(pedido.getObservaciones())
                .createdAt(pedido.getCreatedAt())
                .updatedAt(pedido.getUpdatedAt())
                .build();
    }

    /**
     * Buscar pedido por código de tracking
     */
    public PedidoResponse buscarPorTracking(String codigo) {
        log.debug("Buscando pedido por tracking: {}", codigo);

        Pedido pedido = pedidoRepository.findByCodigoTracking(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "codigoTracking", codigo));

        return toResponse(pedido);
    }

    /**
     * Buscar pedidos por rango de fechas estimadas
     */
    public Page<PedidoResponse> buscarPorFechaEstimada(LocalDate desde, LocalDate hasta, Pageable pageable) {
        log.debug("Buscando pedidos por fecha estimada entre {} y {}", desde, hasta);

        return pedidoRepository.findByFechaEstimadaLlegadaBetween(desde, hasta, pageable)
                .map(this::toResponse);
    }

    /**
     * Cambiar estado del pedido con comentario
     */
    @Transactional
    public PedidoResponse cambiarEstado(Long id, EstadoPedido nuevoEstado, String comentario) {
        log.info("Cambiando estado del pedido {} a {} - comentario: {}", id, nuevoEstado, comentario);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        pedido.cambiarEstado(nuevoEstado, comentario);
        Pedido updated = pedidoRepository.save(pedido);

        return toResponse(updated);
    }

    /**
     * Eliminar pedido (soft delete)
     */
    @Transactional
    public void eliminarPedido(Long id, String reason) {
        log.info("Eliminando pedido ID: {} con motivo: {}", id, reason);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        // Validar que no esté entregado
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new BusinessException("No se puede eliminar un pedido entregado");
        }

        pedido.softDelete(reason);
        pedidoRepository.save(pedido);
        log.info("Pedido eliminado exitosamente: {}", id);
    }

    /**
     * Calcular costo del pedido (MEJORADO)
     */
    @Transactional
    public PedidoResponse calcularCosto(Long id) {
        log.info("Calculando costo del pedido ID: {}", id);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        // Tarifas base
        Double costoPorKg = 50.0;  // USD por kg
        Double costoPorM3 = 100.0; // USD por m3
        Double tarifaBase = 200.0; // Tarifa mínima

        Double costoTotal = tarifaBase;

        // Costo por peso
        if (pedido.getPesoTotalKg() != null && pedido.getPesoTotalKg() > 0) {
            costoTotal += pedido.getPesoTotalKg() * costoPorKg;
        }

        // Costo por volumen
        if (pedido.getVolumenTotalM3() != null && pedido.getVolumenTotalM3() > 0) {
            costoTotal += pedido.getVolumenTotalM3() * costoPorM3;
        }

        // Costo de seguro (2% del valor declarado)
        if (pedido.getRequiereSeguro() && pedido.getValorDeclarado() != null) {
            Double costoSeguro = pedido.getValorDeclarado() * 0.02;
            costoTotal += costoSeguro;
        }

        // Recargo por urgencia (si llega en menos de 7 días)
        if (pedido.getFechaEstimadaLlegada() != null) {
            long diasHastaLlegada = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), pedido.getFechaEstimadaLlegada()
            );
            if (diasHastaLlegada < 7 && diasHastaLlegada > 0) {
                costoTotal *= 1.15; // 15% de recargo
                log.debug("Aplicando recargo por urgencia (15%) - días hasta llegada: {}", diasHastaLlegada);
            }
        }

        // Actualizar observaciones con el costo calculado
        String obs = pedido.getObservaciones() != null ? pedido.getObservaciones() : "";
        pedido.setObservaciones(obs + " [Costo estimado: USD " + String.format("%.2f", costoTotal) + "]");

        Pedido updated = pedidoRepository.save(pedido);
        log.info("Costo calculado para pedido {}: USD {}", id, costoTotal);

        return toResponse(updated);
    }

    /**
     * Verificar si el pedido está atrasado
     */
    public boolean estaAtrasado(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        // Solo aplicable a pedidos no entregados
        if (pedido.getEstado() == EstadoPedido.ENTREGADO ||
            pedido.getEstado() == EstadoPedido.CANCELADO) {
            return false;
        }

        if (pedido.getFechaEstimadaLlegada() == null) {
            return false;
        }

        return LocalDate.now().isAfter(pedido.getFechaEstimadaLlegada());
    }

    /**
     * Calcular días de retraso
     */
    public long calcularDiasRetraso(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        if (!estaAtrasado(id)) {
            return 0;
        }

        return java.time.temporal.ChronoUnit.DAYS.between(
            pedido.getFechaEstimadaLlegada(), LocalDate.now()
        );
    }

    /**
     * Listar pedidos atrasados
     */
    public List<PedidoResponse> listarPedidosAtrasados() {
        log.debug("Listando pedidos atrasados");

        LocalDate hoy = LocalDate.now();
        return pedidoRepository.findByFechaEstimadaLlegadaBetween(
            LocalDate.of(2020, 1, 1), hoy
        ).stream()
                .filter(pedido -> pedido.getEstado() != EstadoPedido.ENTREGADO)
                .filter(pedido -> pedido.getEstado() != EstadoPedido.CANCELADO)
                .map(this::toResponse)
                .toList();
    }

    /**
     * Validar capacidad del container
     */
    @Transactional
    public void validarCapacidadContainer(Long pedidoId) {
        log.info("Validando capacidad de container para pedido: {}", pedidoId);

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", pedidoId));

        // Capacidades estándar de containers
        final Double CAPACIDAD_20FT_KG = 28000.0;
        final Double CAPACIDAD_40FT_KG = 27000.0;
        final Double CAPACIDAD_20FT_M3 = 33.0;
        final Double CAPACIDAD_40FT_M3 = 67.0;

        if (pedido.getPesoTotalKg() != null) {
            if (pedido.getPesoTotalKg() > CAPACIDAD_40FT_KG) {
                throw new BusinessException(
                    String.format("El peso total (%.2f kg) excede la capacidad máxima de un container 40FT (%.2f kg)",
                    pedido.getPesoTotalKg(), CAPACIDAD_40FT_KG)
                );
            }

            if (pedido.getPesoTotalKg() > CAPACIDAD_20FT_KG) {
                log.warn("Pedido {} requiere container 40FT - peso: {} kg", pedidoId, pedido.getPesoTotalKg());
            }
        }

        if (pedido.getVolumenTotalM3() != null) {
            if (pedido.getVolumenTotalM3() > CAPACIDAD_40FT_M3) {
                throw new BusinessException(
                    String.format("El volumen total (%.2f m³) excede la capacidad máxima de un container 40FT (%.2f m³)",
                    pedido.getVolumenTotalM3(), CAPACIDAD_40FT_M3)
                );
            }

            if (pedido.getVolumenTotalM3() > CAPACIDAD_20FT_M3) {
                log.warn("Pedido {} requiere container 40FT - volumen: {} m³", pedidoId, pedido.getVolumenTotalM3());
            }
        }

        log.info("Validación de capacidad completada para pedido: {}", pedidoId);
    }

    /**
     * Marcar pedidos urgentes (llegan en menos de 3 días)
     */
    public List<PedidoResponse> listarPedidosUrgentes() {
        log.debug("Listando pedidos urgentes (llegan en menos de 3 días)");

        LocalDate hoy = LocalDate.now();
        LocalDate dentroTresDias = hoy.plusDays(3);

        return pedidoRepository.findByFechaEstimadaLlegadaBetween(hoy, dentroTresDias)
                .stream()
                .filter(pedido -> pedido.getEstado() != EstadoPedido.ENTREGADO)
                .filter(pedido -> pedido.getEstado() != EstadoPedido.CANCELADO)
                .filter(pedido -> pedido.getEstado() != EstadoPedido.DEVUELTO)
                .map(this::toResponse)
                .toList();
    }

    /**
     * Registrar incidencia en el pedido
     */
    @Transactional
    public PedidoResponse registrarIncidencia(Long id, String tipoIncidencia, String descripcion) {
        log.info("Registrando incidencia en pedido {}: {}", id, tipoIncidencia);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        String incidencia = String.format("\n[INCIDENCIA - %s] %s: %s",
            LocalDateTime.now().toString(),
            inputSanitizer.sanitize(tipoIncidencia),
            inputSanitizer.sanitize(descripcion)
        );

        String obs = pedido.getObservaciones() != null ? pedido.getObservaciones() : "";
        pedido.setObservaciones(obs + incidencia);

        Pedido updated = pedidoRepository.save(pedido);
        log.info("Incidencia registrada exitosamente en pedido: {}", id);

        return toResponse(updated);
    }
}
