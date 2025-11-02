package com.logistic.control.repository;

import com.logistic.control.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findByCodigoNcm(String codigoNcm);

    List<Producto> findByPaisOrigen(String paisOrigen);

    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "p.codigo LIKE CONCAT('%', :search, '%') OR " +
           "p.codigoNcm LIKE CONCAT('%', :search, '%')")
    List<Producto> searchProductos(@Param("search") String search);

    @Query("SELECT p FROM Producto p WHERE p.isActive = true")
    List<Producto> findAllActive();
}
