package com.togamma.heladeria.repository.caja;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.togamma.heladeria.model.caja.MovimientoCaja;

public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    Page<MovimientoCaja> findByCajaId(Long id, Pageable pageable);

    List<MovimientoCaja> findByCajaId(Long id);
}
