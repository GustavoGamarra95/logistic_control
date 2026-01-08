package com.logistic.control.repository;

import com.logistic.control.entity.DetalleDevolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para DetalleDevolucion.
 */
@Repository
public interface DetalleDevolucionRepository extends JpaRepository<DetalleDevolucion, Long> {

    List<DetalleDevolucion> findByDevolucionId(Long devolucionId);

    List<DetalleDevolucion> findByProductoId(Long productoId);

    List<DetalleDevolucion> findByDetalleFacturaId(Long detalleFacturaId);

    List<DetalleDevolucion> findByDetallePedidoId(Long detallePedidoId);

    @Query("SELECT d FROM DetalleDevolucion d WHERE d.devolucion.id = :devolucionId AND d.isActive = true")
    List<DetalleDevolucion> findActiveByDevolucionId(@Param("devolucionId") Long devolucionId);

    @Query("SELECT SUM(d.cantidad) FROM DetalleDevolucion d WHERE d.detalleFactura.id = :detalleFacturaId " +
           "AND d.devolucion.estado IN ('APROBADA', 'EN_PROCESO', 'COMPLETADA') AND d.isActive = true")
    Integer sumCantidadDevueltaByDetalleFactura(@Param("detalleFacturaId") Long detalleFacturaId);
}
