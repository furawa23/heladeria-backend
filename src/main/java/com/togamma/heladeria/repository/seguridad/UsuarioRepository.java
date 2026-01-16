package com.togamma.heladeria.repository.seguridad;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.SucursalScopedRepository;

public interface UsuarioRepository extends SucursalScopedRepository<Usuario> {

    Optional<Usuario> findByUsername(String username);
    Page<Usuario> findBySucursalEmpresaId(Long idEmpresa, Pageable pageable);
}
