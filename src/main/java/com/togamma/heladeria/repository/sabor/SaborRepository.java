package com.togamma.heladeria.repository.sabor;

import com.togamma.heladeria.model.sabores.Sabor;
import com.togamma.heladeria.repository.EmpresaScopedRepository;

public interface SaborRepository extends EmpresaScopedRepository<Sabor> {
    Boolean existsByNombreAndEmpresaId(String nombre, Long id);

    Boolean existsByNombreAndEmpresaIdAndIdNot(String nombre, Long idEmpresa, Long id);

}
