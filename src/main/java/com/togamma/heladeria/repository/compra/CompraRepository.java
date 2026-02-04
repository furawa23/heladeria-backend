package com.togamma.heladeria.repository.compra;

import java.util.Optional;

import com.togamma.heladeria.model.compra.Compra;
import com.togamma.heladeria.repository.SucursalScopedRepository;

public interface CompraRepository extends SucursalScopedRepository<Compra> {

    Optional<Compra> findByIdAndSucursalId(Long id, Long idSucursal);
    Boolean existsByNumeroComprobanteAndSucursalId(String numeroComprobante, Long id);

}
