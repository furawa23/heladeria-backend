package com.togamma.heladeria.repository.seguridad;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.BaseRepository;

public interface UsuarioRepository extends BaseRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
    Page<Usuario> findBySucursalId(Long idSucursal, Pageable pageable);
    Page<Usuario> findBySucursalEmpresaId(Long idEmpresa, Pageable pageable);
}
