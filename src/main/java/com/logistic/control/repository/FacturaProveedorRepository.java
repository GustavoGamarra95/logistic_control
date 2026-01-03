package com.logistic.control.repository;

import com.logistic.control.entity.FacturaProveedor;
import com.logistic.control.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad FacturaProveedor
 */
@Repository
public interface FacturaProveedorRepository extends JpaRepository<FacturaProveedor, Long> {

    /**
     * Busca todas las facturas de un proveedor específico
     */
    List<FacturaProveedor> findByProveedor(Proveedor proveedor);

    /**
     * Busca todas las facturas de un proveedor por ID
     */
    List<FacturaProveedor> findByProveedorId(Long proveedorId);

    /**
     * Busca facturas por estado de pago
     */
    List<FacturaProveedor> findByPagada(Boolean pagada);

    /**
     * Busca facturas activas de un proveedor
     */
    @Query("SELECT f FROM FacturaProveedor f WHERE f.proveedor.id = :proveedorId AND f.isActive = true")
    List<FacturaProveedor> findActiveByProveedorId(@Param("proveedorId") Long proveedorId);

    /**
     * Busca facturas pendientes de pago
     */
    @Query("SELECT f FROM FacturaProveedor f WHERE f.pagada = false AND f.isActive = true")
    List<FacturaProveedor> findPendientesDePago();

    /**
     * Busca facturas vencidas
     */
    @Query("SELECT f FROM FacturaProveedor f WHERE f.pagada = false AND f.fechaVencimiento < :fecha AND f.isActive = true")
    List<FacturaProveedor> findVencidas(@Param("fecha") LocalDate fecha);

    /**
     * Busca facturas próximas a vencer
     */
    @Query("SELECT f FROM FacturaProveedor f WHERE f.pagada = false AND f.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin AND f.isActive = true")
    List<FacturaProveedor> findProximasAVencer(@Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin);

    /**
     * Busca factura por número
     */
    Optional<FacturaProveedor> findByNumeroFactura(String numeroFactura);

    /**
     * Calcula el total adeudado a un proveedor
     */
    @Query("SELECT SUM(f.total) FROM FacturaProveedor f WHERE f.proveedor.id = :proveedorId AND f.pagada = false AND f.isActive = true")
    Double calculateTotalAdeudadoByProveedorId(@Param("proveedorId") Long proveedorId);

    /**
     * Calcula el total de todas las facturas pendientes
     */
    @Query("SELECT SUM(f.total) FROM FacturaProveedor f WHERE f.pagada = false AND f.isActive = true")
    Double calculateTotalPendiente();

    /**
     * Busca facturas en un rango de fechas de emisión
     */
    @Query("SELECT f FROM FacturaProveedor f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    List<FacturaProveedor> findByFechaEmisionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca facturas por método de pago
     */
    List<FacturaProveedor> findByMetodoPago(String metodoPago);

    /**
     * Cuenta facturas pendientes de un proveedor
     */
    @Query("SELECT COUNT(f) FROM FacturaProveedor f WHERE f.proveedor.id = :proveedorId AND f.pagada = false AND f.isActive = true")
    Long countPendientesByProveedorId(@Param("proveedorId") Long proveedorId);
}
