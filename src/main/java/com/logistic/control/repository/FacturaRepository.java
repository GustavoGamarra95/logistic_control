package com.logistic.control.repository;

import com.logistic.control.entity.Factura;
import com.logistic.control.enums.EstadoFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Factura
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    Optional<Factura> findByNumeroFactura(String numeroFactura);

    Optional<Factura> findByCdc(String cdc);

    List<Factura> findByClienteId(Long clienteId);

    List<Factura> findByEstado(EstadoFactura estado);

    List<Factura> findByEstadoIn(List<EstadoFactura> estados);

    Optional<Factura> findByPedidoId(Long pedidoId);

    @Query("SELECT f FROM Factura f WHERE f.cliente.id = :clienteId AND f.estado = :estado")
    List<Factura> findByClienteIdAndEstado(@Param("clienteId") Long clienteId,
                                             @Param("estado") EstadoFactura estado);

    @Query("SELECT f FROM Factura f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    List<Factura> findByFechaEmisionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                              @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT f FROM Factura f WHERE f.estado IN ('APROBADA', 'PAGADA_PARCIAL') AND f.saldo > 0")
    List<Factura> findFacturasPendientesPago();

    @Query("SELECT f FROM Factura f WHERE f.estado = 'APROBADA' AND f.saldo > 0 AND " +
           "f.fechaVencimiento < :fecha")
    List<Factura> findFacturasVencidas(@Param("fecha") java.time.LocalDate fecha);

    @Query("SELECT SUM(f.total) FROM Factura f WHERE f.cliente.id = :clienteId AND f.estado = 'APROBADA'")
    Double sumTotalByCliente(@Param("clienteId") Long clienteId);

    @Query("SELECT SUM(f.saldo) FROM Factura f WHERE f.cliente.id = :clienteId AND f.saldo > 0")
    Double sumSaldoByCliente(@Param("clienteId") Long clienteId);

    @Query("SELECT SUM(f.total) FROM Factura f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND f.estado = 'APROBADA'")
    Double sumTotalByPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio,
                              @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT f FROM Factura f WHERE f.cdc IS NOT NULL AND f.estado = 'APROBADA'")
    List<Factura> findFacturasAprobadas();

    @Query("SELECT f FROM Factura f WHERE f.estado = 'ENVIADA_SIFEN' AND " +
           "f.fechaEnvioSifen < :fechaLimite")
    List<Factura> findFacturasPendientesRespuesta(@Param("fechaLimite") LocalDateTime fechaLimite);

    @Query("SELECT COUNT(f) FROM Factura f WHERE f.estado = :estado")
    Long countByEstado(@Param("estado") EstadoFactura estado);

    @Query("SELECT COUNT(f) FROM Factura f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    Long countByPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio,
                        @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT f FROM Factura f WHERE " +
           "f.numeroFactura LIKE CONCAT('%', :search, '%') OR " +
           "f.cdc LIKE CONCAT('%', :search, '%') OR " +
           "f.cliente.razonSocial LIKE CONCAT('%', :search, '%')")
    List<Factura> searchFacturas(@Param("search") String search);

    @Query("SELECT MAX(CAST(SUBSTRING(f.numeroFactura, 9) AS integer)) FROM Factura f WHERE " +
           "f.establecimiento = :establecimiento AND f.puntoExpedicion = :puntoExpedicion")
    Integer findMaxNumeroFactura(@Param("establecimiento") String establecimiento,
                                  @Param("puntoExpedicion") String puntoExpedicion);
}
