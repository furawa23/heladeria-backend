package com.togamma.heladeria.repository.caja;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.model.caja.MovimientoCaja;
import com.togamma.heladeria.repository.SucursalScopedRepository;

public interface MovimientoCajaRepository extends SucursalScopedRepository<MovimientoCaja> {
    Page<MovimientoCaja> findByCajaId(Long id, Pageable pageable);

    List<MovimientoCaja> findByCajaId(Long id);
}
