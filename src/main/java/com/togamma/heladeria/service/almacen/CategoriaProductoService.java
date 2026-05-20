package com.togamma.heladeria.service.almacen;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.almacen.CategoriaProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.CategoriaProdResponseDTO;

public interface CategoriaProductoService {
    CategoriaProdResponseDTO crear(CategoriaProdRequestDTO dto);
    Page<CategoriaProdResponseDTO> listarTodas(Pageable pageable);
    CategoriaProdResponseDTO obtenerPorId(Long id); 
    CategoriaProdResponseDTO actualizar(Long id, CategoriaProdRequestDTO dto);
    void eliminar(Long id);
}
