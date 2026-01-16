package com.togamma.heladeria.repository.seguridad;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.togamma.heladeria.model.seguridad.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
    Page<Usuario> findBySucursalId(Long idSucursal, Pageable pageable);
    Page<Usuario> findBySucursalEmpresaId(Long idEmpresa, Pageable pageable);
}
