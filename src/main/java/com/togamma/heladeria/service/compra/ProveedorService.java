package com.togamma.heladeria.service.compra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.compra.ProveedorRequestDTO;
import com.togamma.heladeria.dto.response.compra.ProveedorResponseDTO;

public interface ProveedorService {
    ProveedorResponseDTO crear(ProveedorRequestDTO dto);
    Page<ProveedorResponseDTO> listarTodas(Pageable pageable);
    ProveedorResponseDTO obtenerPorId(Long id);
    ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO dto);
    void eliminar(Long id);
}
