package com.togamma.heladeria.service.compra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.compra.CompraRequestDTO;
import com.togamma.heladeria.dto.response.compra.CompraResponseDTO;

public interface CompraService {
    CompraResponseDTO crear(CompraRequestDTO dto);
    Page<CompraResponseDTO> listarTodas(Pageable pageable);
    CompraResponseDTO obtenerPorId(Long id);
    CompraResponseDTO actualizar(Long id, CompraRequestDTO dto);
    void eliminar(Long id);
    void cancelar(Long id);
    void confirmar(Long id);
}
