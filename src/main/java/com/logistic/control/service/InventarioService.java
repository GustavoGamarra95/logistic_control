package com.logistic.control.service;

import com.logistic.control.dto.request.InventarioRequest;
import com.logistic.control.dto.response.InventarioResponse;
import com.logistic.control.entity.Cliente;
import com.logistic.control.entity.Inventario;
import com.logistic.control.entity.Producto;
import com.logistic.control.enums.EstadoInventario;
import com.logistic.control.exception.ResourceNotFoundException;
import com.logistic.control.exception.BusinessException;
import com.logistic.control.repository.ClienteRepository;
import com.logistic.control.repository.InventarioRepository;
import com.logistic.control.repository.ProductoRepository;
import com.logistic.control.security.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de Inventario
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final InputSanitizer inputSanitizer;

    /**
     * Listar todo el inventario con paginación
     */
    public Page<InventarioResponse> listarInventario(Pageable pageable) {
        log.debug("Listando inventario - página: {}", pageable.getPageNumber());
        return inventarioRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtener inventario por ID
     */
    public InventarioResponse obtenerInventario(Long id) {
        log.debug("Obteniendo inventario con ID: {}", id);
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", id));
        return toResponse(inventario);
    }

    /**
     * Buscar inventario por producto
     */
    public List<InventarioResponse> buscarPorProducto(Long productoId) {
        log.debug("Buscando inventario del producto: {}", productoId);
        return inventarioRepository.findByProductoId(productoId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Buscar inventario por cliente
     */
    public List<InventarioResponse> buscarPorCliente(Long clienteId) {
        log.debug("Buscando inventario del cliente: {}", clienteId);
        return inventarioRepository.findByClienteId(clienteId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Buscar inventario por ubicación
     */
    public List<InventarioResponse> buscarPorUbicacion(String ubicacion) {
        log.debug("Buscando inventario por ubicación: {}", ubicacion);
        String ubicacionSanitized = inputSanitizer.sanitize(ubicacion);
        return inventarioRepository.findByUbicacionDeposito(ubicacionSanitized).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Buscar inventario por estado
     */
    public List<InventarioResponse> buscarPorEstado(EstadoInventario estado) {
        log.debug("Buscando inventario con estado: {}", estado);
        return inventarioRepository.findByEstado(estado).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Crear nuevo registro de inventario
     */
    @Transactional
    public InventarioResponse crearInventario(InventarioRequest request) {
        log.info("Creando registro de inventario para producto: {}", request.getProductoId());

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClienteId()));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductoId()));

        // Validar datos
        validarDatosInventario(request);

        // Crear registro de inventario
        Inventario inventario = Inventario.builder()
                .cliente(cliente)
                .producto(producto)
                .cantidad(request.getCantidad())
                .ubicacionDeposito(inputSanitizer.sanitize(request.getUbicacionDeposito()))
                .zona(request.getZona())
                .pasillo(request.getPasillo())
                .rack(request.getRack())
                .nivel(request.getNivel())
                .lote(inputSanitizer.sanitize(request.getLote()))
                .fechaVencimiento(request.getFechaVencimiento())
                .costoAlmacenajeDiario(request.getCostoAlmacenajeDiario())
                .estado(EstadoInventario.DISPONIBLE)
                .observaciones(inputSanitizer.sanitize(request.getObservaciones()))
                .build();

        Inventario saved = inventarioRepository.save(inventario);

        log.info("Inventario creado exitosamente con ID: {}", saved.getId());
        return toResponse(saved);
    }

    /**
     * Actualizar inventario existente
     */
    @Transactional
    public InventarioResponse actualizarInventario(Long id, InventarioRequest request) {
        log.info("Actualizando inventario ID: {}", id);

        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", id));

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClienteId()));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductoId()));

        validarDatosInventario(request);

        // Actualizar campos
        inventario.setCliente(cliente);
        inventario.setProducto(producto);
        inventario.setUbicacionDeposito(inputSanitizer.sanitize(request.getUbicacionDeposito()));
        inventario.setZona(request.getZona());
        inventario.setPasillo(request.getPasillo());
        inventario.setRack(request.getRack());
        inventario.setNivel(request.getNivel());
        inventario.setCantidad(request.getCantidad());
        inventario.setLote(inputSanitizer.sanitize(request.getLote()));
        inventario.setFechaVencimiento(request.getFechaVencimiento());
        inventario.setCostoAlmacenajeDiario(request.getCostoAlmacenajeDiario());
        inventario.setObservaciones(inputSanitizer.sanitize(request.getObservaciones()));

        Inventario updated = inventarioRepository.save(inventario);
        log.info("Inventario actualizado exitosamente: {}", updated.getId());

        return toResponse(updated);
    }

    /**
     * Registrar ingreso de mercadería
     */
    @Transactional
    public InventarioResponse registrarIngreso(InventarioRequest request) {
        log.info("Registrando ingreso de inventario para producto: {}", request.getProductoId());

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductoId()));

        // Validar datos
        validarDatosInventario(request);

        // Crear registro de inventario
        Inventario inventario = Inventario.builder()
                .producto(producto)
                .cantidad(request.getCantidad())
                .ubicacionDeposito(inputSanitizer.sanitize(request.getUbicacionDeposito()))
                .lote(inputSanitizer.sanitize(request.getLote()))
                .fechaVencimiento(request.getFechaVencimiento())
                .estado(EstadoInventario.DISPONIBLE)
                .observaciones(inputSanitizer.sanitize(request.getObservaciones()))
                .build();

        // Llamar al método entrada() que establece fechaEntrada y estado
        inventario.entrada();

        Inventario saved = inventarioRepository.save(inventario);

        log.info("Ingreso de inventario registrado exitosamente con ID: {}", saved.getId());
        return toResponse(saved);
    }

    /**
     * Registrar entrada (marca fecha de entrada)
     */
    @Transactional
    public InventarioResponse registrarEntrada(Long id) {
        log.info("Registrando entrada de inventario ID: {}", id);

        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", id));

        inventario.entrada();
        Inventario updated = inventarioRepository.save(inventario);

        log.info("Entrada registrada exitosamente");
        return toResponse(updated);
    }

    /**
     * Registrar salida de mercadería
     */
    @Transactional
    public InventarioResponse registrarSalida(Long inventarioId, Integer cantidad) {
        log.info("Registrando salida de inventario ID: {}, Cantidad: {}", inventarioId, cantidad);

        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", inventarioId));

        // Validar estado
        if (inventario.getEstado() != EstadoInventario.DISPONIBLE &&
            inventario.getEstado() != EstadoInventario.EN_DEPOSITO) {
            throw new BusinessException("El inventario no está disponible para salida");
        }

        // Usar el método salida() de la entidad
        try {
            inventario.salida(cantidad);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(e.getMessage());
        }

        Inventario updated = inventarioRepository.save(inventario);

        log.info("Salida de inventario registrada exitosamente");
        return toResponse(updated);
    }

    /**
     * Marcar inventario como reservado
     */
    @Transactional
    public InventarioResponse reservar(Long inventarioId, Integer cantidad) {
        log.info("Reservando inventario ID: {}, Cantidad: {}", inventarioId, cantidad);

        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", inventarioId));

        if (inventario.getEstado() != EstadoInventario.DISPONIBLE) {
            throw new BusinessException("El inventario no está disponible para reservar");
        }

        try {
            inventario.reservar(cantidad);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(e.getMessage());
        }

        Inventario updated = inventarioRepository.save(inventario);

        log.info("Inventario reservado exitosamente");
        return toResponse(updated);
    }

    /**
     * Liberar reserva de inventario
     */
    @Transactional
    public InventarioResponse liberarReserva(Long inventarioId, Integer cantidad) {
        log.info("Liberando reserva de inventario ID: {}, Cantidad: {}", inventarioId, cantidad);

        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", inventarioId));

        if (inventario.getEstado() != EstadoInventario.RESERVADO) {
            throw new BusinessException("El inventario no está reservado");
        }

        try {
            inventario.liberarReserva(cantidad);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(e.getMessage());
        }

        Inventario updated = inventarioRepository.save(inventario);

        log.info("Reserva de inventario liberada exitosamente");
        return toResponse(updated);
    }

    /**
     * Eliminar inventario
     */
    @Transactional
    public void eliminarInventario(Long id) {
        log.info("Eliminando inventario ID: {}", id);

        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", id));

        inventarioRepository.delete(inventario);

        log.info("Inventario eliminado exitosamente: {}", id);
    }

    /**
     * Marcar inventario como dañado
     */
    @Transactional
    public void marcarComoDanado(Long inventarioId, String motivo) {
        log.info("Marcando inventario ID: {} como dañado", inventarioId);
        
        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", inventarioId));
        
        inventario.setEstado(EstadoInventario.DANIADO);
        
        String obs = inventario.getObservaciones() != null ? inventario.getObservaciones() + " | " : "";
        inventario.setObservaciones(obs + "DAÑADO: " + motivo);
        
        inventarioRepository.save(inventario);
        
        log.info("Inventario marcado como dañado");
    }

    /**
     * Validar datos de inventario (MEJORADO)
     */
    private void validarDatosInventario(InventarioRequest request) {
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new BusinessException("La cantidad debe ser mayor a cero");
        }

        // Validar cantidad máxima razonable
        if (request.getCantidad() > 100000) {
            throw new BusinessException("La cantidad excede el límite máximo permitido (100,000 unidades)");
        }

        if (request.getFechaVencimiento() != null &&
            request.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            throw new BusinessException("La fecha de vencimiento no puede ser pasada");
        }

        // Validar ubicación
        if (request.getUbicacionDeposito() != null && request.getUbicacionDeposito().length() > 100) {
            throw new BusinessException("La ubicación no puede exceder 100 caracteres");
        }

        // Validar costo de almacenaje
        if (request.getCostoAlmacenajeDiario() != null && request.getCostoAlmacenajeDiario() < 0) {
            throw new BusinessException("El costo de almacenaje no puede ser negativo");
        }
    }

    /**
     * Listar inventario próximo a vencer (en los próximos 30 días)
     */
    public List<InventarioResponse> listarProximoAVencer(int dias) {
        log.debug("Listando inventario que vence en los próximos {} días", dias);

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusDays(dias);

        return inventarioRepository.findAll().stream()
                .filter(inventario -> inventario.getFechaVencimiento() != null)
                .filter(inventario -> inventario.getFechaVencimiento().isAfter(ahora))
                .filter(inventario -> inventario.getFechaVencimiento().isBefore(limite))
                .filter(inventario -> inventario.getEstado() == EstadoInventario.DISPONIBLE)
                .map(this::toResponse)
                .toList();
    }

    /**
     * Listar inventario vencido
     */
    public List<InventarioResponse> listarVencido() {
        log.debug("Listando inventario vencido");

        LocalDateTime ahora = LocalDateTime.now();

        return inventarioRepository.findAll().stream()
                .filter(inventario -> inventario.getFechaVencimiento() != null)
                .filter(inventario -> inventario.getFechaVencimiento().isBefore(ahora))
                .filter(inventario -> inventario.getEstado() != EstadoInventario.DANIADO)
                .map(this::toResponse)
                .toList();
    }

    /**
     * Calcular valor total del inventario
     */
    public Double calcularValorTotal(Long inventarioId) {
        log.debug("Calculando valor total del inventario: {}", inventarioId);

        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", inventarioId));

        Producto producto = inventario.getProducto();

        if (producto.getValorUnitario() == null) {
            log.warn("Producto {} no tiene valor unitario definido", producto.getId());
            return 0.0;
        }

        Double valorTotal = inventario.getCantidad() * producto.getValorUnitario();

        log.debug("Valor total del inventario {}: {} {}", inventarioId, valorTotal, producto.getMoneda());

        return valorTotal;
    }

    /**
     * Validar ubicación del inventario
     */
    @Transactional
    public void validarUbicacion(Long inventarioId) {
        log.info("Validando ubicación del inventario: {}", inventarioId);

        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", inventarioId));

        // Validar que la ubicación esté completa
        if (inventario.getUbicacionDeposito() == null || inventario.getUbicacionDeposito().isBlank()) {
            throw new BusinessException("La ubicación del depósito es requerida");
        }

        // Validar coordenadas si están presentes
        if (inventario.getZona() != null && inventario.getZona().isBlank()) {
            throw new BusinessException("La zona no puede estar vacía");
        }

        // Validación de productos especiales
        Producto producto = inventario.getProducto();

        if (producto.getRequiereRefrigeracion() &&
            (inventario.getZona() == null || !inventario.getZona().toUpperCase().contains("FRIO"))) {
            log.warn("Producto {} requiere refrigeración pero no está en zona FRIO", producto.getId());
        }

        if (producto.getEsPeligroso() &&
            (inventario.getZona() == null || !inventario.getZona().toUpperCase().contains("PELIGRO"))) {
            log.warn("Producto {} es peligroso pero no está en zona PELIGROSO", producto.getId());
        }

        log.info("Validación de ubicación completada para inventario: {}", inventarioId);
    }

    /**
     * Calcular costo total de almacenaje acumulado
     */
    public Double calcularCostoAlmacenajeTotal(Long inventarioId) {
        log.debug("Calculando costo total de almacenaje para inventario: {}", inventarioId);

        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", inventarioId));

        // El método getCostoAlmacenajeTotal() ya está en la entidad
        Double costoTotal = inventario.getCostoAlmacenajeTotal();

        log.debug("Costo total de almacenaje para inventario {}: {}", inventarioId, costoTotal);

        return costoTotal != null ? costoTotal : 0.0;
    }

    /**
     * Alertar sobre inventario con rotación baja (más de 90 días sin movimiento)
     */
    public List<InventarioResponse> listarConBajaRotacion() {
        log.debug("Listando inventario con baja rotación (>90 días sin movimiento)");

        LocalDateTime limite = LocalDateTime.now().minusDays(90);

        return inventarioRepository.findAll().stream()
                .filter(inventario -> inventario.getFechaEntrada() != null)
                .filter(inventario -> inventario.getFechaEntrada().isBefore(limite))
                .filter(inventario -> inventario.getFechaSalida() == null)
                .filter(inventario -> inventario.getEstado() == EstadoInventario.EN_DEPOSITO)
                .map(this::toResponse)
                .toList();
    }

    /**
     * Aplicar rotación FIFO - obtener el lote más antiguo disponible de un producto
     */
    public InventarioResponse obtenerLoteFIFO(Long productoId) {
        log.debug("Obteniendo lote FIFO para producto: {}", productoId);

        List<Inventario> inventarios = inventarioRepository.findByProductoId(productoId);

        Inventario masAntiguo = inventarios.stream()
                .filter(inv -> inv.getEstado() == EstadoInventario.DISPONIBLE)
                .filter(inv -> inv.getCantidadDisponible() > 0)
                .min((inv1, inv2) -> {
                    if (inv1.getFechaEntrada() == null) return 1;
                    if (inv2.getFechaEntrada() == null) return -1;
                    return inv1.getFechaEntrada().compareTo(inv2.getFechaEntrada());
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No hay inventario disponible para producto", "productoId", productoId));

        log.info("Lote FIFO encontrado: ID={}, FechaEntrada={}, Cantidad={}",
            masAntiguo.getId(), masAntiguo.getFechaEntrada(), masAntiguo.getCantidadDisponible());

        return toResponse(masAntiguo);
    }

    /**
     * Generar alerta de stock bajo
     */
    public boolean verificarStockBajo(Long productoId, Integer umbralMinimo) {
        log.debug("Verificando stock bajo para producto: {}, umbral: {}", productoId, umbralMinimo);

        List<Inventario> inventarios = inventarioRepository.findByProductoId(productoId);

        Integer stockTotal = inventarios.stream()
                .filter(inv -> inv.getEstado() == EstadoInventario.DISPONIBLE ||
                              inv.getEstado() == EstadoInventario.EN_DEPOSITO)
                .mapToInt(Inventario::getCantidadDisponible)
                .sum();

        boolean stockBajo = stockTotal < umbralMinimo;

        if (stockBajo) {
            log.warn("ALERTA: Stock bajo para producto {}. Stock actual: {}, Umbral: {}",
                productoId, stockTotal, umbralMinimo);
        }

        return stockBajo;
    }

    /**
     * Consolidar inventarios del mismo producto en la misma ubicación
     */
    @Transactional
    public InventarioResponse consolidarInventarios(List<Long> inventariosIds) {
        log.info("Consolidando {} inventarios", inventariosIds.size());

        if (inventariosIds == null || inventariosIds.size() < 2) {
            throw new BusinessException("Se requieren al menos 2 inventarios para consolidar");
        }

        List<Inventario> inventarios = inventariosIds.stream()
                .map(id -> inventarioRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Inventario", "id", id)))
                .toList();

        // Validar que todos sean del mismo producto
        Long productoId = inventarios.get(0).getProducto().getId();
        boolean mismoProducto = inventarios.stream()
                .allMatch(inv -> inv.getProducto().getId().equals(productoId));

        if (!mismoProducto) {
            throw new BusinessException("Todos los inventarios deben ser del mismo producto");
        }

        // Validar que todos estén en la misma ubicación
        String ubicacion = inventarios.get(0).getUbicacionDeposito();
        boolean mismaUbicacion = inventarios.stream()
                .allMatch(inv -> inv.getUbicacionDeposito().equals(ubicacion));

        if (!mismaUbicacion) {
            throw new BusinessException("Todos los inventarios deben estar en la misma ubicación");
        }

        // Consolidar en el primer inventario
        Inventario principal = inventarios.get(0);
        int cantidadTotal = principal.getCantidad();

        for (int i = 1; i < inventarios.size(); i++) {
            Inventario inv = inventarios.get(i);
            cantidadTotal += inv.getCantidad();
            inventarioRepository.delete(inv);
        }

        principal.setCantidad(cantidadTotal);
        String obs = principal.getObservaciones() != null ? principal.getObservaciones() : "";
        principal.setObservaciones(obs + String.format(" [CONSOLIDADO: %d inventarios]", inventarios.size()));

        Inventario updated = inventarioRepository.save(principal);
        log.info("Inventarios consolidados exitosamente. Cantidad total: {}", cantidadTotal);

        return toResponse(updated);
    }

    /**
     * Convertir entidad a DTO de respuesta
     */
    private InventarioResponse toResponse(Inventario inventario) {
        return InventarioResponse.builder()
                .id(inventario.getId())
                .clienteId(inventario.getCliente() != null ? inventario.getCliente().getId() : null)
                .clienteNombre(inventario.getCliente() != null ? inventario.getCliente().getRazonSocial() : null)
                .productoId(inventario.getProducto().getId())
                .productoDescripcion(inventario.getProducto().getDescripcion())
                .ubicacionDeposito(inventario.getUbicacionDeposito())
                .zona(inventario.getZona())
                .pasillo(inventario.getPasillo())
                .rack(inventario.getRack())
                .nivel(inventario.getNivel())
                .cantidad(inventario.getCantidad())
                .cantidadReservada(inventario.getCantidadReservada())
                .cantidadDisponible(inventario.getCantidadDisponible())
                .estado(inventario.getEstado())
                .fechaEntrada(inventario.getFechaEntrada())
                .fechaSalida(inventario.getFechaSalida())
                .lote(inventario.getLote())
                .fechaVencimiento(inventario.getFechaVencimiento())
                .diasAlmacenaje(inventario.getDiasAlmacenaje())
                .costoAlmacenajeDiario(inventario.getCostoAlmacenajeDiario())
                .costoAlmacenajeTotal(inventario.getCostoAlmacenajeTotal())
                .observaciones(inventario.getObservaciones())
                .createdAt(inventario.getCreatedAt())
                .updatedAt(inventario.getUpdatedAt())
                .build();
    }
}
