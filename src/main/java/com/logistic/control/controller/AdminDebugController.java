package com.logistic.control.controller;

import com.logistic.control.entity.Usuario;
import com.logistic.control.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller temporal para debug - REMOVER EN PRODUCCIÓN
 */
@Slf4j
@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
@Hidden // Ocultar de Swagger
public class AdminDebugController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/reset-test-passwords")
    public ResponseEntity<?> resetTestPasswords() {
        String newPassword = "demo123";
        String[] testUsers = {"admin", "operador", "cliente1", "finanzas", "deposito"};
        
        Map<String, Object> result = new HashMap<>();
        result.put("action", "reset_passwords");
        result.put("newPassword", newPassword);
        
        for (String username : testUsers) {
            usuarioRepository.findByUsername(username).ifPresent(user -> {
                String oldHash = user.getPassword().substring(0, Math.min(30, user.getPassword().length()));
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setEnabled(true);
                user.setAccountNonLocked(true);
                user.setAccountNonExpired(true);
                user.setCredentialsNonExpired(true);
                user.setFailedLoginAttempts(0);
                usuarioRepository.save(user);
                
                String newHash = user.getPassword().substring(0, Math.min(30, user.getPassword().length()));
                log.info("Reset password for user: {} | Old hash: {} | New hash: {}", 
                        username, oldHash, newHash);
                result.put(username, "✓ Password reset");
            });
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/verify-users")
    public ResponseEntity<?> verifyUsers() {
        String[] testUsers = {"admin", "operador", "cliente1", "finanzas", "deposito"};
        Map<String, Object> result = new HashMap<>();
        
        for (String username : testUsers) {
            Map<String, Object> userInfo = new HashMap<>();
            usuarioRepository.findByUsername(username).ifPresentOrElse(user -> {
                userInfo.put("exists", true);
                userInfo.put("enabled", user.isEnabled());
                userInfo.put("accountNonLocked", user.isAccountNonLocked());
                userInfo.put("passwordHash", user.getPassword().substring(0, Math.min(30, user.getPassword().length())) + "...");
                userInfo.put("email", user.getEmail());
                userInfo.put("roles", user.getRoles());
                result.put(username, userInfo);
            }, () -> {
                userInfo.put("exists", false);
                result.put(username, userInfo);
            });
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/test-encode")
    public ResponseEntity<?> testEncode(@RequestParam(defaultValue = "demo123") String password) {
        String hash = passwordEncoder.encode(password);
        boolean matches = passwordEncoder.matches(password, hash);
        
        Map<String, Object> result = new HashMap<>();
        result.put("password", password);
        result.put("hash", hash);
        result.put("matches", matches);
        result.put("hashLength", hash.length());
        result.put("algorithm", hash.substring(0, 4));
        
        return ResponseEntity.ok(result);
    }
}
