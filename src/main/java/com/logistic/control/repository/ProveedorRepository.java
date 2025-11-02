package com.logistic.control.repository;

import com.logistic.control.entity.Proveedor;
import com.logistic.control.enums.TipoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByRuc(String ruc);

    List<Proveedor> findByTipo(TipoProveedor tipo);

    @Query("SELECT p FROM Proveedor p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "p.ruc LIKE CONCAT('%', :search, '%')")
    List<Proveedor> searchProveedores(@Param("search") String search);

    @Query("SELECT p FROM Proveedor p WHERE p.isActive = true")
    List<Proveedor> findAllActive();
}
