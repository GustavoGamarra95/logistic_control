package com.logistic.control.repository;

import com.logistic.control.entity.Factura;
import com.logistic.control.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Pago
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /**
     * Busca todos los pagos de una factura específica
     */
    List<Pago> findByFactura(Factura factura);

    /**
     * Busca todos los pagos de una factura por ID
     */
    List<Pago> findByFacturaId(Long facturaId);

    /**
     * Busca pagos por método de pago
     */
    List<Pago> findByMetodoPago(String metodoPago);

    /**
     * Busca pagos en un rango de fechas
     */
    @Query("SELECT p FROM Pago p WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    List<Pago> findByFechaPagoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                       @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Calcula el total pagado de una factura
     */
    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.factura.id = :facturaId")
    Double calculateTotalPagadoByFacturaId(@Param("facturaId") Long facturaId);

    /**
     * Busca pagos por moneda
     */
    List<Pago> findByMoneda(String moneda);

    /**
     * Busca pagos por referencia
     */
    List<Pago> findByReferenciaContaining(String referencia);

    /**
     * Busca pagos ordenados por fecha descendente
     */
    @Query("SELECT p FROM Pago p ORDER BY p.fechaPago DESC")
    List<Pago> findAllOrderedByFechaPagoDesc();

    /**
     * Cuenta los pagos de una factura
     */
    @Query("SELECT COUNT(p) FROM Pago p WHERE p.factura.id = :facturaId")
    Long countByFacturaId(@Param("facturaId") Long facturaId);

    /**
     * Busca pagos por banco
     */
    List<Pago> findByBanco(String banco);
}
