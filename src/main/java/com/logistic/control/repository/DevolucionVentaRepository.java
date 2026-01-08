package com.logistic.control.repository;

import com.logistic.control.entity.DevolucionVenta;
import com.logistic.control.enums.EstadoDevolucion;
import com.logistic.control.enums.TipoDevolucion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para DevolucionVenta.
 */
@Repository
public interface DevolucionVentaRepository extends JpaRepository<DevolucionVenta, Long> {

    Optional<DevolucionVenta> findByNumeroDevolucion(String numeroDevolucion);

    List<DevolucionVenta> findByEstado(EstadoDevolucion estado);

    List<DevolucionVenta> findByTipo(TipoDevolucion tipo);

    List<DevolucionVenta> findByClienteId(Long clienteId);

    Page<DevolucionVenta> findByClienteId(Long clienteId, Pageable pageable);

    List<DevolucionVenta> findByFacturaId(Long facturaId);

    List<DevolucionVenta> findByPedidoId(Long pedidoId);

    @Query("SELECT d FROM DevolucionVenta d WHERE d.estado = 'SOLICITADA' " +
           "AND d.isActive = true ORDER BY d.fechaSolicitud ASC")
    List<DevolucionVenta> findDevolucionesPendientes();

    @Query("SELECT d FROM DevolucionVenta d WHERE d.estado IN :estados " +
           "AND d.isActive = true ORDER BY d.fechaSolicitud DESC")
    List<DevolucionVenta> findByEstadoIn(@Param("estados") List<EstadoDevolucion> estados);

    @Query("SELECT d FROM DevolucionVenta d WHERE d.fechaSolicitud BETWEEN :fechaInicio AND :fechaFin " +
           "AND d.isActive = true")
    List<DevolucionVenta> findByFechaSolicitudBetween(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("SELECT COUNT(d) FROM DevolucionVenta d WHERE d.estado = :estado AND d.isActive = true")
    Long countByEstado(@Param("estado") EstadoDevolucion estado);

    @Query("SELECT COUNT(d) FROM DevolucionVenta d WHERE d.cliente.id = :clienteId " +
           "AND d.estado = :estado AND d.isActive = true")
    Long countByClienteIdAndEstado(@Param("clienteId") Long clienteId, @Param("estado") EstadoDevolucion estado);
}
