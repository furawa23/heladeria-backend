package com.togamma.heladeria.config;

import com.togamma.heladeria.model.seguridad.Rol; // <-- Asegúrate de tener este import
import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.seguridad.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscar el usuario en la BD
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 2. Lógica de validación (Soft Delete)
        boolean isEnabled = true;

        if (usuario.getDeletedAt() != null) {
            isEnabled = false;
        }

        if (usuario.getRol() != Rol.SUPERADMIN) {
            // Si la empresa tiene fecha de eliminación, bloqueamos al dueño y a sus empleados
            if (usuario.getEmpresa() != null && usuario.getEmpresa().getDeletedAt() != null) {
                isEnabled = false;
            }
            // Si la sucursal tiene fecha de eliminación, bloqueamos solo a los empleados de esa sucursal
            else if (usuario.getRol() == Rol.EMPLEADO && usuario.getSucursal() != null && usuario.getSucursal().getDeletedAt() != null) {
                isEnabled = false;
            }
        }

        // 3. Convertir tu Rol (Enum) a GrantedAuthority de Spring
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name());

        // Para evitar problemas con usuarios de Google que no tienen contraseña
        String password = usuario.getPassword() != null ? usuario.getPassword() : "";

        // 4. Retornar el objeto User de Spring
        return new User(
                usuario.getUsername(),
                password,
                isEnabled, // <-- AQUÍ INSERTAMOS LA VALIDACIÓN
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(authority)
        );
    }
}