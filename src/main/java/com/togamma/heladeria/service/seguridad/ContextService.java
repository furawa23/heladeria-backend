package com.togamma.heladeria.service.seguridad;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.togamma.heladeria.model.seguridad.Empresa;
import com.togamma.heladeria.model.seguridad.Sucursal;
import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.seguridad.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContextService {

    private final UsuarioRepository usuarioRepository;

    public Usuario getUsuarioLogueado() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("No se encontró al usuario de la sesión actual"));
    }

    public Empresa getEmpresaLogueada() {
        
        Empresa empresaActual = getUsuarioLogueado().getEmpresa();

        if (empresaActual == null) {
             throw new RuntimeException("El usuario logueado no pertenece a ninguna empresa");
        }

        return empresaActual;
    }

    public Sucursal getSucursalLogueada() {
        
        Sucursal sucursalActual = getUsuarioLogueado().getSucursal();

        if (sucursalActual == null) {
             throw new RuntimeException("El usuario logueado no pertenece a ninguna sucursal");
        }

        return sucursalActual;
    }

    public Sucursal getSucursalLogueadaOrNull() {
        try {
            return getSucursalLogueada();
        } catch (RuntimeException e) {
            return null;
        }
    }

}
