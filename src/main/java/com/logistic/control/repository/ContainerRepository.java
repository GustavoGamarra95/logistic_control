package com.logistic.control.repository;

import com.logistic.control.entity.Container;
import com.logistic.control.enums.TipoContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

    Optional<Container> findByNumero(String numero);

    List<Container> findByTipo(TipoContainer tipo);

    List<Container> findByConsolidado(Boolean consolidado);

    List<Container> findByEnTransito(Boolean enTransito);

    List<Container> findByEnAduana(Boolean enAduana);

    @Query("SELECT c FROM Container c WHERE c.enTransito = true AND c.isActive = true")
    List<Container> findContainersEnTransito();

    @Query("SELECT c FROM Container c WHERE c.enAduana = true AND c.isActive = true")
    List<Container> findContainersEnAduana();

    @Query("SELECT c FROM Container c WHERE c.fechaLlegadaEstimada BETWEEN :fechaInicio AND :fechaFin")
    List<Container> findByFechaLlegadaEstimadaBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                        @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT c FROM Container c JOIN c.clientes cl WHERE cl.id = :clienteId")
    List<Container> findByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT c FROM Container c WHERE c.empresaTransporte = :empresa")
    List<Container> findByEmpresaTransporte(@Param("empresa") String empresa);

    @Query("SELECT c FROM Container c WHERE " +
           "c.numero LIKE CONCAT('%', :search, '%') OR " +
           "c.numeroBl LIKE CONCAT('%', :search, '%') OR " +
           "c.empresaTransporte LIKE CONCAT('%', :search, '%')")
    List<Container> searchContainers(@Param("search") String search);

    @Query("SELECT COUNT(c) FROM Container c WHERE c.enTransito = true")
    Long countEnTransito();

    @Query("SELECT COUNT(c) FROM Container c WHERE c.enAduana = true")
    Long countEnAduana();

    @Query("SELECT c FROM Container c WHERE c.consolidado = false AND c.isActive = true")
    List<Container> findContainersSinConsolidar();

    @Query("SELECT c FROM Container c WHERE " +
           "(c.pesoMaximoKg - c.pesoKg) >= :pesoRequerido AND " +
           "c.consolidado = false AND c.isActive = true")
    List<Container> findContainersConCapacidadDisponible(@Param("pesoRequerido") Double pesoRequerido);
    
    List<Container> findByEnTransitoTrue();
    
    List<Container> findByEnPuertoTrue();
}
