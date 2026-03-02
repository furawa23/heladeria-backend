package com.togamma.heladeria.repository.venta;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import com.togamma.heladeria.model.venta.Mesa;
import com.togamma.heladeria.repository.SucursalScopedRepository;

public interface MesaRepository extends SucursalScopedRepository<Mesa> {

    boolean existsByNumeroAndSucursalId(Integer numero, Long id);

    @Override
    @EntityGraph(attributePaths = {"sucursal"})
    Page<Mesa> findBySucursalId(Long idSucursal, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"sucursal"})
    Optional<Mesa> findByIdAndSucursalId(Long id, Long idSucursal);
}