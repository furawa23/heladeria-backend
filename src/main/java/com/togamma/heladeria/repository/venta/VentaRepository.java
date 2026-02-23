package com.togamma.heladeria.repository.venta;

import com.togamma.heladeria.model.venta.Venta;
import com.togamma.heladeria.repository.SucursalScopedRepository;

public interface VentaRepository extends SucursalScopedRepository<Venta> {
    Boolean existsByNumeroComprobanteAndSucursalId(String numeroComprobante, Long id);

}
