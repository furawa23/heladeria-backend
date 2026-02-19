package com.togamma.heladeria.repository.venta;

import com.togamma.heladeria.model.venta.Mesa;
import com.togamma.heladeria.repository.SucursalScopedRepository;

public interface MesaRepository extends SucursalScopedRepository<Mesa> {

    boolean existsByNumeroAndSucursalId(Integer numero, Long id);

}
