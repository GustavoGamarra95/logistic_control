package com.logistic.control.repository;

import com.logistic.control.entity.DetalleFactura;
import com.logistic.control.entity.Factura;
import com.logistic.control.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad DetalleFactura
 */
@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {

    /**
     * Busca todos los detalles de una factura específica
     */
    List<DetalleFactura> findByFactura(Factura factura);

    /**
     * Busca todos los detalles de una factura por ID
     */
    List<DetalleFactura> findByFacturaId(Long facturaId);

    /**
     * Busca todos los detalles que contienen un producto específico
     */
    List<DetalleFactura> findByProducto(Producto producto);

    /**
     * Busca detalles por producto ID
     */
    List<DetalleFactura> findByProductoId(Long productoId);

    /**
     * Cuenta los items de una factura
     */
    @Query("SELECT COUNT(d) FROM DetalleFactura d WHERE d.factura.id = :facturaId")
    Long countByFacturaId(@Param("facturaId") Long facturaId);

    /**
     * Calcula el total de una factura sumando todos sus detalles
     */
    @Query("SELECT SUM(d.total) FROM DetalleFactura d WHERE d.factura.id = :facturaId")
    Double calculateTotalByFacturaId(@Param("facturaId") Long facturaId);

    /**
     * Obtiene los detalles ordenados por descripción
     */
    @Query("SELECT d FROM DetalleFactura d WHERE d.factura.id = :facturaId ORDER BY d.descripcion")
    List<DetalleFactura> findByFacturaIdOrderedByDescripcion(@Param("facturaId") Long facturaId);
}
