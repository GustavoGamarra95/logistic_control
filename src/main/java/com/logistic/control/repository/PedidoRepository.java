package com.logistic.control.repository;

import com.logistic.control.entity.Pedido;
import com.logistic.control.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Pedido
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByCodigoTracking(String codigoTracking);

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByEstadoIn(List<EstadoPedido> estados);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.estado = :estado")
    List<Pedido> findByClienteIdAndEstado(@Param("clienteId") Long clienteId,
                                           @Param("estado") EstadoPedido estado);

    @Query("SELECT p FROM Pedido p WHERE p.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    List<Pedido> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                             @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pedido p WHERE p.fechaEstimadaLlegada <= :fecha AND p.estado IN :estados")
    List<Pedido> findPedidosConLlegadaEstimada(@Param("fecha") LocalDate fecha,
                                                 @Param("estados") List<EstadoPedido> estados);

    @Query("SELECT p FROM Pedido p WHERE p.estado IN " +
           "('EN_TRANSITO', 'RECIBIDO', 'EN_ADUANA') AND p.isActive = true")
    List<Pedido> findPedidosEnTransito();

    @Query("SELECT p FROM Pedido p WHERE p.estado = 'EN_ADUANA' AND p.isActive = true")
    List<Pedido> findPedidosEnAduana();

    @Query("SELECT p FROM Pedido p WHERE p.numeroContenedorGuia = :numeroContainer")
    List<Pedido> findByNumeroContainer(@Param("numeroContainer") String numeroContainer);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.cliente.id = :clienteId AND p.estado = :estado")
    Long countByClienteIdAndEstado(@Param("clienteId") Long clienteId,
                                    @Param("estado") EstadoPedido estado);

    @Query("SELECT p FROM Pedido p WHERE " +
           "p.codigoTracking LIKE CONCAT('%', :search, '%') OR " +
           "p.descripcionMercaderia LIKE CONCAT('%', :search, '%') OR " +
           "p.numeroContenedorGuia LIKE CONCAT('%', :search, '%')")
    List<Pedido> searchPedidos(@Param("search") String search);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long countByEstado(@Param("estado") EstadoPedido estado);

    List<Pedido> findByFechaEstimadaLlegadaBetween(LocalDate desde, LocalDate hasta);

    Page<Pedido> findByFechaEstimadaLlegadaBetween(LocalDate desde, LocalDate hasta, Pageable pageable);

    @Query(value = "SELECT p FROM Pedido p LEFT JOIN FETCH p.cliente WHERE p.isActive = true",
           countQuery = "SELECT COUNT(p) FROM Pedido p WHERE p.isActive = true")
    Page<Pedido> findAllWithCliente(Pageable pageable);

    /**
     * Encuentra pedidos que tienen al menos un ítem pendiente de facturar.
     * Útil para mostrar en el módulo de facturación.
     */
    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.detalles d " +
           "WHERE d.cantidad > d.cantidadFacturada " +
           "AND p.estado NOT IN ('CANCELADO', 'DEVUELTO', 'FACTURADO') " +
           "AND p.isActive = true")
    List<Pedido> findPedidosPendientesDeFacturar();

    /**
     * Verifica si un pedido está completamente facturado.
     * Retorna true si NO hay ningún detalle con cantidad pendiente.
     */
    @Query("SELECT CASE WHEN COUNT(d) = 0 THEN true ELSE false END " +
           "FROM DetallePedido d WHERE d.pedido.id = :pedidoId " +
           "AND d.cantidad > d.cantidadFacturada " +
           "AND d.isActive = true")
    Boolean isPedidoCompletamenteFacturado(@Param("pedidoId") Long pedidoId);

    /**
     * Encuentra pedidos pendientes de facturar con paginación y cliente cargado.
     */
    @Query(value = "SELECT DISTINCT p FROM Pedido p " +
           "LEFT JOIN FETCH p.cliente " +
           "JOIN p.detalles d " +
           "WHERE d.cantidad > d.cantidadFacturada " +
           "AND p.estado NOT IN ('CANCELADO', 'DEVUELTO', 'FACTURADO') " +
           "AND p.isActive = true",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Pedido p " +
                       "JOIN p.detalles d " +
                       "WHERE d.cantidad > d.cantidadFacturada " +
                       "AND p.estado NOT IN ('CANCELADO', 'DEVUELTO', 'FACTURADO') " +
                       "AND p.isActive = true")
    Page<Pedido> findPedidosPendientesDeFacturar(Pageable pageable);
}
