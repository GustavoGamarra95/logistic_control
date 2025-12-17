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
}
