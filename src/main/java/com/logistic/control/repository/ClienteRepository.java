package com.logistic.control.repository;

import com.logistic.control.entity.Cliente;
import com.logistic.control.enums.TipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByRuc(String ruc);

    Optional<Cliente> findByEmail(String email);

    List<Cliente> findByTipoServicio(TipoServicio tipoServicio);

    List<Cliente> findByPais(String pais);

    @Query("SELECT c FROM Cliente c WHERE c.isActive = true")
    List<Cliente> findAllActive();

    @Query("SELECT c FROM Cliente c WHERE c.esFacturadorElectronico = true")
    List<Cliente> findAllFacturadoresElectronicos();

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.razonSocial) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.nombreFantasia) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "c.ruc LIKE CONCAT('%', :search, '%')")
    List<Cliente> searchClientes(@Param("search") String search);

    @Query("SELECT c FROM Cliente c WHERE c.creditoDisponible < :limite")
    List<Cliente> findByCreditoDisponibleLessThan(@Param("limite") Double limite);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.isActive = true")
    Long countActiveClientes();
}
