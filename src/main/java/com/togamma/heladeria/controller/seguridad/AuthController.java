package com.togamma.heladeria.controller.seguridad;

import com.togamma.heladeria.dto.request.seguridad.LoginRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.AuthResponseDTO;
import com.togamma.heladeria.service.seguridad.impl.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}