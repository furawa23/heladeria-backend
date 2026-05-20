package com.togamma.heladeria.service.venta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.venta.MesaRequestDTO;
import com.togamma.heladeria.dto.response.venta.MesaResponseDTO;

public interface MesaService {
    MesaResponseDTO crear(MesaRequestDTO dto);
    Page<MesaResponseDTO> listarTodas(Pageable pageable);
    MesaResponseDTO obtenerPorId(Long id); 
    MesaResponseDTO actualizar(Long id, MesaRequestDTO dto);
    void eliminar(Long id);
}
