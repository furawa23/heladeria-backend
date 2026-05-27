package com.togamma.heladeria.controller.seguridad;

import com.togamma.heladeria.dto.request.seguridad.LoginRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.AuthResponseDTO;
import com.togamma.heladeria.dto.response.seguridad.UsuarioResponseDTO;
import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.seguridad.UsuarioRepository;
import com.togamma.heladeria.service.seguridad.impl.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository; 

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioActual(Authentication authentication) {
        // 1. Obtenemos el username del token
        String username = authentication.getName();

        // 2. Buscamos al usuario en la BD
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. ¡Usamos tu método estático perfecto del record!
        UsuarioResponseDTO response = UsuarioResponseDTO.mapToResponse(usuario);

        return ResponseEntity.ok(response);
    }
}