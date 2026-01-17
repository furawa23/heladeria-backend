package com.togamma.heladeria.repository.almacen;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.repository.EmpresaScopedRepository;

public interface ProductoRepository extends EmpresaScopedRepository<Producto> {
    
    Page<Producto> findByEmpresaIdAndSeVendeTrue(Long id, Pageable pageable);
    Page<Producto> findByEmpresaIdAndSeVendeFalse(Long id, Pageable pageable);
    Boolean existsByNombreAndEmpresaId(String nombre, Long id);
    Page<Producto> findByCategoria(Pageable pageable);
}
