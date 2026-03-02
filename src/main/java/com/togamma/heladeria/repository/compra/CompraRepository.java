package com.togamma.heladeria.repository.compra;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import com.togamma.heladeria.model.compra.Compra;
import com.togamma.heladeria.repository.SucursalScopedRepository;

public interface CompraRepository extends SucursalScopedRepository<Compra> {
    
    Boolean existsByNumeroComprobanteAndSucursalId(String numeroComprobante, Long id);

    @Override
    @EntityGraph(attributePaths = {"proveedor", "detalles.producto", "detalles.presentacion"})
    Optional<Compra> findByIdAndSucursalId(Long id, Long idSucursal);

    @Override
    @EntityGraph(attributePaths = {"proveedor", "detalles.producto", "detalles.presentacion"})
    Page<Compra> findBySucursalId(Long idSucursal, Pageable pageable);
}