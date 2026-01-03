package com.logistic.control.repository;

import com.logistic.control.entity.DetallePedido;
import com.logistic.control.entity.Pedido;
import com.logistic.control.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad DetallePedido
 */
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    /**
     * Busca todos los detalles de un pedido específico
     */
    List<DetallePedido> findByPedido(Pedido pedido);

    /**
     * Busca todos los detalles de un pedido por ID
     */
    List<DetallePedido> findByPedidoId(Long pedidoId);

    /**
     * Busca todos los detalles que contienen un producto específico
     */
    List<DetallePedido> findByProducto(Producto producto);

    /**
     * Busca todos los detalles activos de un pedido
     */
    @Query("SELECT d FROM DetallePedido d WHERE d.pedido.id = :pedidoId AND d.isActive = true")
    List<DetallePedido> findActiveByPedidoId(@Param("pedidoId") Long pedidoId);

    /**
     * Cuenta los items de un pedido
     */
    @Query("SELECT COUNT(d) FROM DetallePedido d WHERE d.pedido.id = :pedidoId AND d.isActive = true")
    Long countByPedidoId(@Param("pedidoId") Long pedidoId);

    /**
     * Elimina todos los detalles de un pedido (soft delete)
     */
    @Query("UPDATE DetallePedido d SET d.isActive = false, d.deletedAt = CURRENT_TIMESTAMP WHERE d.pedido.id = :pedidoId")
    void softDeleteByPedidoId(@Param("pedidoId") Long pedidoId);
}
