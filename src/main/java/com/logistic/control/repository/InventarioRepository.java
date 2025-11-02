package com.logistic.control.repository;

import com.logistic.control.entity.Inventario;
import com.logistic.control.enums.EstadoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para Inventario
 */
@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    List<Inventario> findByClienteId(Long clienteId);

    List<Inventario> findByProductoId(Long productoId);

    List<Inventario> findByContainerId(Long containerId);

    List<Inventario> findByEstado(EstadoInventario estado);

    List<Inventario> findByUbicacionDeposito(String ubicacion);

    @Query("SELECT i FROM Inventario i WHERE i.cliente.id = :clienteId AND i.estado = :estado")
    List<Inventario> findByClienteIdAndEstado(@Param("clienteId") Long clienteId,
                                                @Param("estado") EstadoInventario estado);

    @Query("SELECT i FROM Inventario i WHERE i.cliente.id = :clienteId AND i.cantidadDisponible > 0")
    List<Inventario> findStockDisponibleByCliente(@Param("clienteId") Long clienteId);

    @Query("SELECT i FROM Inventario i WHERE i.producto.id = :productoId AND i.cantidadDisponible > 0")
    List<Inventario> findStockDisponibleByProducto(@Param("productoId") Long productoId);

    @Query("SELECT SUM(i.cantidad) FROM Inventario i WHERE i.cliente.id = :clienteId AND i.estado = 'EN_DEPOSITO'")
    Integer sumCantidadByCliente(@Param("clienteId") Long clienteId);

    @Query("SELECT SUM(i.cantidad) FROM Inventario i WHERE i.producto.id = :productoId AND i.estado = 'EN_DEPOSITO'")
    Integer sumCantidadByProducto(@Param("productoId") Long productoId);

    @Query("SELECT i FROM Inventario i WHERE i.estado = 'EN_DEPOSITO' AND i.isActive = true")
    List<Inventario> findAllEnDeposito();

    @Query("SELECT i FROM Inventario i WHERE i.estado = 'RETENIDO_ADUANA' AND i.isActive = true")
    List<Inventario> findAllRetenidosAduana();

    @Query("SELECT i FROM Inventario i WHERE i.cantidadDisponible < :threshold")
    List<Inventario> findBajoStock(@Param("threshold") Integer threshold);

    @Query("SELECT i FROM Inventario i WHERE i.zona = :zona AND i.estado = 'EN_DEPOSITO'")
    List<Inventario> findByZona(@Param("zona") String zona);

    @Query("SELECT DISTINCT i.zona FROM Inventario i WHERE i.zona IS NOT NULL")
    List<String> findAllZonas();

    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.estado = 'EN_DEPOSITO'")
    Long countEnDeposito();

    @Query("SELECT SUM(i.cantidadDisponible) FROM Inventario i WHERE i.estado = 'DISPONIBLE'")
    Long countTotalDisponible();
}
