package com.logistic.control.service;

import com.logistic.control.dto.request.UsuarioRequest;
import com.logistic.control.dto.response.UsuarioResponse;
import com.logistic.control.entity.Usuario;
import com.logistic.control.enums.Role;
import com.logistic.control.exception.ResourceNotFoundException;
import com.logistic.control.exception.DuplicateResourceException;
import com.logistic.control.exception.BusinessException;
import com.logistic.control.repository.UsuarioRepository;
import com.logistic.control.security.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestión de Usuarios
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final InputSanitizer inputSanitizer;

    /**
     * Listar todos los usuarios con paginación
     */
    public Page<UsuarioResponse> listarUsuarios(Pageable pageable) {
        log.debug("Listando usuarios - página: {}", pageable.getPageNumber());
        return usuarioRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtener usuario por ID
     */
    public UsuarioResponse obtenerUsuario(Long id) {
        log.debug("Obteniendo usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return toResponse(usuario);
    }

    /**
     * Buscar usuario por username
     */
    public UsuarioResponse buscarPorUsername(String username) {
        String usernameSanitized = inputSanitizer.sanitize(username);
        log.debug("Buscando usuario por username: {}", usernameSanitized);

        Usuario usuario = usuarioRepository.findByUsername(usernameSanitized)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", usernameSanitized));
        return toResponse(usuario);
    }

    /**
     * Buscar usuario por email
     */
    public UsuarioResponse buscarPorEmail(String email) {
        String emailSanitized = inputSanitizer.sanitizeForXss(email);
        log.debug("Buscando usuario por email: {}", emailSanitized);

        Usuario usuario = usuarioRepository.findByEmail(emailSanitized)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", emailSanitized));
        return toResponse(usuario);
    }

    /**
     * Listar usuarios activos
     */
    public List<UsuarioResponse> listarActivos() {
        log.debug("Listando usuarios activos");
        return usuarioRepository.findByEnabledTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Crear nuevo usuario
     */
    @Transactional
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        log.info("Creando nuevo usuario: {}", request.getUsername());
        
        // Validar duplicados
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Usuario", "username", request.getUsername());
        }
        
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Usuario", "email", request.getEmail());
        }
        
        // Validar datos
        validarDatosUsuario(request);
        
        // Crear entidad
        Usuario usuario = Usuario.builder()
                .username(inputSanitizer.sanitize(request.getUsername()))
                .password(passwordEncoder.encode(request.getPassword()))
                .email(inputSanitizer.sanitizeForXss(request.getEmail()))
                .nombre(inputSanitizer.sanitize(request.getNombre()))
                .apellido(inputSanitizer.sanitize(request.getApellido()))
                .telefono(inputSanitizer.sanitize(request.getTelefono()))
                .enabled(true)
                .build();
        
        // Agregar roles si se proporcionan
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            request.getRoles().forEach(usuario::addRole);
        } else {
            usuario.addRole(Role.OPERADOR); // Rol por defecto
        }
        
        Usuario saved = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", saved.getId());
        
        return toResponse(saved);
    }

    /**
     * Actualizar usuario existente
     */
    @Transactional
    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request) {
        log.info("Actualizando usuario ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        // Validar duplicados (excepto el mismo usuario)
        validarDuplicadosExcepto(id, request.getUsername(), request.getEmail());
        validarDatosUsuario(request);
        
        // Actualizar campos
        usuario.setUsername(inputSanitizer.sanitize(request.getUsername()));
        usuario.setEmail(inputSanitizer.sanitizeForXss(request.getEmail()));
        usuario.setNombre(inputSanitizer.sanitize(request.getNombre()));
        usuario.setApellido(inputSanitizer.sanitize(request.getApellido()));
        usuario.setTelefono(inputSanitizer.sanitize(request.getTelefono()));
        
        // Actualizar roles si se proporcionan
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            usuario.getRoles().clear();
            request.getRoles().forEach(usuario::addRole);
        }
        
        // Solo actualizar password si se proporciona uno nuevo
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        Usuario updated = usuarioRepository.save(usuario);
        log.info("Usuario actualizado exitosamente: {}", updated.getId());
        
        return toResponse(updated);
    }

    /**
     * Desactivar usuario (soft delete)
     */
    @Transactional
    public UsuarioResponse desactivarUsuario(Long id) {
        log.info("Desactivando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setEnabled(false);
        Usuario updated = usuarioRepository.save(usuario);

        log.info("Usuario desactivado exitosamente: {}", id);
        return toResponse(updated);
    }

    /**
     * Activar usuario
     */
    @Transactional
    public UsuarioResponse activarUsuario(Long id) {
        log.info("Activando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setEnabled(true);
        Usuario updated = usuarioRepository.save(usuario);

        log.info("Usuario activado exitosamente: {}", id);
        return toResponse(updated);
    }

    /**
     * Bloquear usuario
     */
    @Transactional
    public UsuarioResponse bloquearUsuario(Long id) {
        log.info("Bloqueando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setAccountNonLocked(false);
        Usuario updated = usuarioRepository.save(usuario);

        log.info("Usuario bloqueado exitosamente: {}", id);
        return toResponse(updated);
    }

    /**
     * Desbloquear usuario
     */
    @Transactional
    public UsuarioResponse desbloquearUsuario(Long id) {
        log.info("Desbloqueando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.unlock();
        Usuario updated = usuarioRepository.save(usuario);

        log.info("Usuario desbloqueado exitosamente: {}", id);
        return toResponse(updated);
    }

    /**
     * Agregar rol a usuario
     */
    @Transactional
    public UsuarioResponse agregarRole(Long id, Role role) {
        log.info("Agregando rol {} a usuario ID: {}", role, id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.addRole(role);
        Usuario updated = usuarioRepository.save(usuario);

        log.info("Rol agregado exitosamente");
        return toResponse(updated);
    }

    /**
     * Remover rol de usuario
     */
    @Transactional
    public UsuarioResponse removerRole(Long id, Role role) {
        log.info("Removiendo rol {} de usuario ID: {}", role, id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.removeRole(role);
        Usuario updated = usuarioRepository.save(usuario);

        log.info("Rol removido exitosamente");
        return toResponse(updated);
    }

    /**
     * Cambiar contraseña
     */
    @Transactional
    public void cambiarPassword(Long id, String passwordActual, String passwordNuevo) {
        log.info("Cambiando contraseña para usuario ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        // Verificar password actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }
        
        // Validar nueva contraseña
        if (passwordNuevo.length() < 8) {
            throw new BusinessException("La nueva contraseña debe tener al menos 8 caracteres");
        }
        
        usuario.setPassword(passwordEncoder.encode(passwordNuevo));
        usuarioRepository.save(usuario);
        
        log.info("Contraseña cambiada exitosamente para usuario: {}", id);
    }

    /**
     * Validar duplicados excepto el mismo usuario
     */
    private void validarDuplicadosExcepto(Long id, String username, String email) {
        usuarioRepository.findByUsername(username).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new DuplicateResourceException("Usuario", "username", username);
            }
        });
        
        usuarioRepository.findByEmail(email).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new DuplicateResourceException("Usuario", "email", email);
            }
        });
    }

    /**
     * Validar datos de negocio
     */
    private void validarDatosUsuario(UsuarioRequest request) {
        if (!inputSanitizer.isValidEmail(request.getEmail())) {
            throw new BusinessException("Formato de email inválido");
        }
        
        if (request.getUsername().length() < 4) {
            throw new BusinessException("El username debe tener al menos 4 caracteres");
        }
        
        if (request.getPassword() != null && request.getPassword().length() < 8) {
            throw new BusinessException("La contraseña debe tener al menos 8 caracteres");
        }
    }

    /**
     * Restablecer contraseña de usuario
     */
    @Transactional
    public UsuarioResponse resetPassword(Long id, String newPassword) {
        log.info("Restableciendo contraseña para usuario ID: {}", id);

        if (newPassword == null || newPassword.length() < 8) {
            throw new BusinessException("La contraseña debe tener al menos 8 caracteres");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuario = usuarioRepository.save(usuario);

        log.info("Contraseña restablecida exitosamente para usuario: {}", usuario.getUsername());
        return toResponse(usuario);
    }

    /**
     * Convertir entidad a DTO de respuesta
     */
    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .nombreCompleto(usuario.getNombreCompleto())
                .telefono(usuario.getTelefono())
                .roles(usuario.getRoles())
                .enabled(usuario.getEnabled())
                .accountNonLocked(usuario.isAccountNonLocked())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}
