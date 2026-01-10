package com.togamma.heladeria.controller.seguridad;

import com.togamma.heladeria.dto.request.seguridad.LoginRequestDTO;
import com.togamma.heladeria.dto.request.seguridad.RegisterRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.AuthResponseDTO;
import com.togamma.heladeria.dto.response.seguridad.UsuarioResponseDTO;
import com.togamma.heladeria.service.seguridad.impl.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Esta ruta debe ser pública (verificar en SecurityConfig)
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.registrar(request));
    }
    
    // Un endpoint simple para probar si el login funciona después
    @GetMapping("/me")
    public ResponseEntity<String> checkSession() {
        return ResponseEntity.ok("¡Estás logueado correctamente!");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}