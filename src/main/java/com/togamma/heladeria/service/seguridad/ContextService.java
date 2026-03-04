package com.togamma.heladeria.service.seguridad;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.togamma.heladeria.model.seguridad.Empresa;
import com.togamma.heladeria.model.seguridad.Sucursal;
import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.seguridad.SucursalRepository; // Añadido
import com.togamma.heladeria.repository.seguridad.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContextService {

    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository; // Añadido
    private final HttpServletRequest request; // Añadido para leer los headers HTTP

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
        
        Usuario usuario = getUsuarioLogueado();
        Sucursal sucursalActual = usuario.getSucursal();

        // 1. Si el usuario es Empleado, ya tiene una sucursal física en la BD. La devolvemos directamente.
        if (sucursalActual != null) {
             return sucursalActual;
        }

        // 2. Si es Dueño (su sucursalActual en BD es null), revisamos si Angular envió la sucursal temporal
        String sucursalIdHeader = request.getHeader("X-Sucursal-Id");

        if (sucursalIdHeader != null && !sucursalIdHeader.isEmpty()) {
            Long idSucursal = Long.parseLong(sucursalIdHeader);

            // Buscamos la sucursal solicitada
            Sucursal sucursalTemporal = sucursalRepository.findById(idSucursal)
                    .orElseThrow(() -> new RuntimeException("La sucursal temporal seleccionada no existe"));

            // SEGURIDAD: Verificamos que esa sucursal realmente pertenezca a la empresa del Dueño
            if (!sucursalTemporal.getEmpresa().getId().equals(usuario.getEmpresa().getId())) {
                throw new RuntimeException("Acceso Denegado: Esta sucursal no pertenece a tu empresa");
            }

            // Si todo está bien, el Dueño asume el rol de esta sucursal durante esta petición
            return sucursalTemporal;
        }

        // 3. Si es Dueño y aún no ha seleccionado ninguna sucursal en su menú superior
        throw new RuntimeException("El usuario logueado no pertenece a ninguna sucursal y no ha seleccionado una temporal");
    }

    public Sucursal getSucursalLogueadaOrNull() {
        try {
            return getSucursalLogueada();
        } catch (RuntimeException e) {
            return null; // Útil para cuando el Dueño lista productos globales sin importar la sucursal
        }
    }
}