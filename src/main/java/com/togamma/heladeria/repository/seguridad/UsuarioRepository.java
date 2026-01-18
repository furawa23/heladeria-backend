package com.togamma.heladeria.repository.seguridad;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.SucursalScopedRepository;

public interface UsuarioRepository extends SucursalScopedRepository<Usuario> {
    
    @Override
    @EntityGraph(attributePaths = {"sucursal", "empresa"})
    Page<Usuario> findAll(Pageable pageable);
    @Override
    @EntityGraph(attributePaths = {"sucursal", "empresa"})
    Optional<Usuario> findById(Long id);
    @Override
    @EntityGraph(attributePaths = {"sucursal", "empresa"})
    Page<Usuario> findBySucursalId(Long idSucursal, Pageable pageable);
    @EntityGraph(attributePaths = {"sucursal", "empresa"})
    Optional<Usuario> findByUsername(String username);
    @EntityGraph(attributePaths = {"sucursal", "empresa"})
    Page<Usuario> findBySucursalEmpresaId(Long idEmpresa, Pageable pageable);
}
