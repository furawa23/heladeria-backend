package com.togamma.heladeria.service.seguridad;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.togamma.heladeria.model.seguridad.Empresa;
import com.togamma.heladeria.model.seguridad.Sucursal;
import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.seguridad.UsuarioRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContextService {

    private final UsuarioRepository usuarioRepository;
    private Usuario usuarioCache;

    public Usuario getUsuarioLogueado() {
        if (usuarioCache != null) {
                return usuarioCache;
        }

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usuarioCache = usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("No se encontró al usuario de la sesión actual"));
        
        return usuarioCache;
    }

    public Empresa getEmpresaLogueada() {
        
        Empresa empresaActual = getUsuarioLogueado().getSucursal().getEmpresa();

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

}
