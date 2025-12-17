package com.logistic.control.controller;

import com.logistic.control.dto.request.AuthRequest;
import com.logistic.control.dto.request.RegisterRequest;
import com.logistic.control.dto.response.AuthResponse;
import com.logistic.control.entity.Usuario;
import com.logistic.control.repository.UsuarioRepository;
import com.logistic.control.security.InputSanitizer;
import com.logistic.control.security.JwtService;
import com.logistic.control.security.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticación JWT con validación, sanitización y rate limiting
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de tokens JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final InputSanitizer inputSanitizer;
    private final RateLimitService rateLimitService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", 
               description = "Crea un nuevo usuario en el sistema. Roles disponibles: ADMIN, OPERADOR, CLIENTE, FINANZAS, DEPOSITO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe"),
        @ApiResponse(responseCode = "429", description = "Demasiados intentos de registro")
    })
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        
        String ipAddress = getClientIp(httpRequest);
        
        // Rate limiting por IP
        if (rateLimitService.isBlocked(ipAddress)) {
            log.warn("Registration blocked due to rate limit from IP: {}", ipAddress);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Demasiados intentos de registro. Intente más tarde.");
        }
        
        // Sanitizar inputs
        String username = inputSanitizer.sanitize(request.getUsername());
        String email = inputSanitizer.sanitize(request.getEmail());
        String nombre = inputSanitizer.sanitize(request.getNombre());
        String apellido = inputSanitizer.sanitize(request.getApellido());
        
        // Validaciones adicionales
        if (!inputSanitizer.isValidEmail(email)) {
            return ResponseEntity.badRequest()
                    .body("El formato del email no es válido");
        }
        
        if (!inputSanitizer.isAlphanumeric(username)) {
            return ResponseEntity.badRequest()
                    .body("El username solo puede contener letras y números");
        }
        
        // Validar longitud de password (mínimo 8 caracteres en producción)
        if (request.getPassword().length() < 8) {
            return ResponseEntity.badRequest()
                    .body("La contraseña debe tener al menos 8 caracteres");
        }
        
        // Verificar si el usuario ya existe
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("El username ya existe");
        }

        if (usuarioRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("El email ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .telefono(request.getTelefono() != null ? 
                         inputSanitizer.sanitize(request.getTelefono()) : null)
                .roles(request.getRoles())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        usuarioRepository.save(usuario);
        
        log.info("New user registered: {} from IP: {}", username, ipAddress);

        // Generar tokens
        String accessToken = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(usuario.getRoles())
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", 
               description = "Autentica usuario y retorna tokens JWT de acceso y refresco")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
        @ApiResponse(responseCode = "403", description = "Cuenta bloqueada"),
        @ApiResponse(responseCode = "429", description = "Demasiados intentos fallidos")
    })
    public ResponseEntity<?> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest httpRequest) {
        
        String ipAddress = getClientIp(httpRequest);
        String username = inputSanitizer.sanitize(request.getUsername());
        String loginKey = username + ":" + ipAddress;
        
        // Verificar si está bloqueado por intentos fallidos
        if (rateLimitService.isBlocked(loginKey)) {
            log.warn("Login blocked for user: {} from IP: {}", username, ipAddress);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Cuenta bloqueada temporalmente por múltiples intentos fallidos. " +
                          "Intente nuevamente en " + 
                          rateLimitService.getTimeUntilUnlock(loginKey).toMinutes() + " minutos.");
        }
        
        try {
            // Autenticar usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // Registrar intento fallido
            rateLimitService.recordFailedLogin(loginKey);
            log.warn("Failed login attempt for user: {} from IP: {}", username, ipAddress);
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        }

        // Cargar usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar si la cuenta está bloqueada
        if (!usuario.isAccountNonLocked()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Cuenta bloqueada. Contacte al administrador.");
        }

        // Login exitoso
        rateLimitService.recordSuccessfulLogin(loginKey);
        usuario.recordSuccessfulLogin();
        usuarioRepository.save(usuario);
        
        log.info("Successful login for user: {} from IP: {}", username, ipAddress);

        // Generar tokens
        String accessToken = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(usuario.getRoles())
                .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token", 
               description = "Genera un nuevo access token usando el refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new RuntimeException("Token de refresh inválido");
        }

        String token = refreshToken.substring(7);
        String username = jwtService.extractUsername(token);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!jwtService.isTokenValid(token, usuario)) {
            throw new RuntimeException("Token de refresh expirado o inválido");
        }

        // Generar nuevos tokens
        String newAccessToken = jwtService.generateToken(usuario);
        String newRefreshToken = jwtService.generateRefreshToken(usuario);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(usuario.getRoles())
                .build());
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual", 
               description = "Retorna la información del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Usuario> getCurrentUser(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String jwt = token.substring(7);
        String username = jwtService.extractUsername(jwt);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(usuario);
    }
    
    /**
     * Obtiene la IP real del cliente considerando proxies
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
