package com.togamma.heladeria.service.sabor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.sabor.SaborRequestDTO;
import com.togamma.heladeria.dto.response.sabor.SaborResponseDTO;

public interface SaborService {
    SaborResponseDTO crear(SaborRequestDTO dto);
    Page<SaborResponseDTO> listarTodas(Pageable pageable);
    SaborResponseDTO obtenerPorId(Long id); 
    SaborResponseDTO actualizar(Long id, SaborRequestDTO dto);
    void eliminar(Long id); 
}