package com.togamma.heladeria.repository.seguridad;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.model.seguridad.Sucursal;
import com.togamma.heladeria.repository.BaseRepository;

public interface SucursalRepository extends BaseRepository<Sucursal, Long> {

    Page<Sucursal> findByEmpresaId(Long idEmpresa, Pageable pageable);
}
