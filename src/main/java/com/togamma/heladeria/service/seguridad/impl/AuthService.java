package com.togamma.heladeria.service.seguridad.impl;

import com.togamma.heladeria.config.JwtService;
import com.togamma.heladeria.dto.request.seguridad.LoginRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.AuthResponseDTO;
import com.togamma.heladeria.dto.response.seguridad.UsuarioResponseDTO;
import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.seguridad.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO login(LoginRequestDTO request) {
        // 1. Autenticar (Esto lanzará excepción si el password es incorrecto)
        var auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
            )
        );
        
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        // 2. Si pasó la línea anterior, el usuario es correcto. Buscamos sus datos.
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow();

        // 4. Preparamos la respuesta con los datos del usuario
        UsuarioResponseDTO usuarioResponse = UsuarioResponseDTO.mapToResponse(usuario);

        // 5. Retornamos Token + Datos
        return new AuthResponseDTO(token, usuarioResponse);
    }
}