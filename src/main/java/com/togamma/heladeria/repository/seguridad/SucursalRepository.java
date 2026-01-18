package com.togamma.heladeria.repository.seguridad;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import com.togamma.heladeria.model.seguridad.Sucursal;
import com.togamma.heladeria.repository.EmpresaScopedRepository;

public interface SucursalRepository extends EmpresaScopedRepository<Sucursal> {

    @Override
    @EntityGraph(attributePaths = {"empresa"})
    Page<Sucursal> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"empresa"})
    Page<Sucursal> findByEmpresaId(Long idEmpresa, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"empresa"})
    Optional<Sucursal> findById(Long id);
}
