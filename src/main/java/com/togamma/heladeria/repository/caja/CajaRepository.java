package com.togamma.heladeria.repository.caja;

import java.util.Optional;

import com.togamma.heladeria.model.caja.Caja;
import com.togamma.heladeria.model.caja.EstadoCaja;
import com.togamma.heladeria.repository.SucursalScopedRepository;

public interface CajaRepository extends SucursalScopedRepository<Caja> {
    Optional<Caja> findBySucursalIdAndEstado(Long idSucursal, EstadoCaja estado);

    Boolean existsBySucursalIdAndEstado(Long idSucursal, EstadoCaja estado);
}
