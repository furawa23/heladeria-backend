package com.togamma.heladeria.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.togamma.heladeria.model.seguridad.Rol;
import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.seguridad.UsuarioRepository;
import com.togamma.heladeria.service.seguridad.ActiveSessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ActiveSessionService activeSessionService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String googleEmail = oAuth2User.getAttribute("email");

        // Buscar o crear usuario
        Usuario usuario = usuarioRepository.findByGoogleEmail(googleEmail)
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario();
                    nuevo.setUsername(googleEmail);
                    nuevo.setPassword("");
                    nuevo.setGoogleEmail(googleEmail);
                    nuevo.setRol(Rol.EMPLEADO);
                    return usuarioRepository.save(nuevo);
                });

        // Cargar como UserDetails (lo que JwtService espera)
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());

        String jwt = jwtService.generateToken(userDetails);

        // Validar que no haya una sesión activa en otro dispositivo
        if (!activeSessionService.registerSession(usuario.getUsername(), jwt)) {
            response.sendRedirect(frontendUrl + "/auth/login?error=active_session");
            return;
        }

        response.sendRedirect(frontendUrl + "/auth/callback?token=" + jwt);
    }
}