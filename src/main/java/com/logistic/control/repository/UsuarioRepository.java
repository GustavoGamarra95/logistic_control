package com.logistic.control.repository;

import com.logistic.control.entity.Usuario;
import com.logistic.control.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = :role")
    List<Usuario> findByRole(@Param("role") Role role);

    @Query("SELECT u FROM Usuario u WHERE u.enabled = true")
    List<Usuario> findAllActive();
    
    List<Usuario> findByEnabledTrue();

    @Query("SELECT u FROM Usuario u WHERE u.cliente.id = :clienteId")
    List<Usuario> findByClienteId(@Param("clienteId") Long clienteId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
