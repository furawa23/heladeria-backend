package com.togamma.heladeria.repository.seguridad;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.togamma.heladeria.model.seguridad.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    @Override
    @EntityGraph(attributePaths = {"sucursales"})
    Page<Empresa> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"sucursales"})
    Optional<Empresa> findById(Long id);
}
