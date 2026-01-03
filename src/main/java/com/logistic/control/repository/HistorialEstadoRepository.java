package com.logistic.control.repository;

import com.logistic.control.entity.HistorialEstado;
import com.logistic.control.entity.Pedido;
import com.logistic.control.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad HistorialEstado
 */
@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

    /**
     * Busca todo el historial de un pedido específico
     */
    List<HistorialEstado> findByPedido(Pedido pedido);

    /**
     * Busca todo el historial de un pedido por ID, ordenado por fecha
     */
    @Query("SELECT h FROM HistorialEstado h WHERE h.pedido.id = :pedidoId ORDER BY h.fechaCambio DESC")
    List<HistorialEstado> findByPedidoIdOrderedByFecha(@Param("pedidoId") Long pedidoId);

    /**
     * Busca el último cambio de estado de un pedido
     */
    @Query("SELECT h FROM HistorialEstado h WHERE h.pedido.id = :pedidoId ORDER BY h.fechaCambio DESC LIMIT 1")
    Optional<HistorialEstado> findLastByPedidoId(@Param("pedidoId") Long pedidoId);

    /**
     * Busca cambios de estado a un estado específico
     */
    List<HistorialEstado> findByEstadoNuevo(EstadoPedido estadoNuevo);

    /**
     * Busca cambios de estado desde un estado específico
     */
    List<HistorialEstado> findByEstadoAnterior(EstadoPedido estadoAnterior);

    /**
     * Busca cambios de estado en un rango de fechas
     */
    @Query("SELECT h FROM HistorialEstado h WHERE h.fechaCambio BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fechaCambio DESC")
    List<HistorialEstado> findByFechaCambioBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                     @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca cambios realizados por un usuario específico
     */
    List<HistorialEstado> findByUsuario(String usuario);

    /**
     * Cuenta los cambios de estado de un pedido
     */
    @Query("SELECT COUNT(h) FROM HistorialEstado h WHERE h.pedido.id = :pedidoId")
    Long countByPedidoId(@Param("pedidoId") Long pedidoId);

    /**
     * Busca todos los cambios a un estado específico en un rango de fechas
     */
    @Query("SELECT h FROM HistorialEstado h WHERE h.estadoNuevo = :estado AND h.fechaCambio BETWEEN :fechaInicio AND :fechaFin")
    List<HistorialEstado> findByEstadoAndFechaBetween(@Param("estado") EstadoPedido estado,
                                                        @Param("fechaInicio") LocalDateTime fechaInicio,
                                                        @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Verifica si un pedido pasó por un estado específico
     */
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HistorialEstado h WHERE h.pedido.id = :pedidoId AND h.estadoNuevo = :estado")
    Boolean hasPassedThroughEstado(@Param("pedidoId") Long pedidoId, @Param("estado") EstadoPedido estado);
}
