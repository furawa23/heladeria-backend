package com.togamma.heladeria.repository.compra;

import com.togamma.heladeria.model.compra.Proveedor;
import com.togamma.heladeria.repository.EmpresaScopedRepository;

public interface ProveedorRepository extends EmpresaScopedRepository<Proveedor> {
    Boolean existsByRazonSocialAndEmpresaId(String nombre, Long id);
    Boolean existsByRucAndEmpresaId(String nombre, Long id);
}
