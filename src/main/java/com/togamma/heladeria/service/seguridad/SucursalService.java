package com.togamma.heladeria.service.seguridad;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.seguridad.SucursalRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.SucursalResponseDTO;

public interface SucursalService {
    SucursalResponseDTO crear(SucursalRequestDTO dto);
    Page<SucursalResponseDTO> listarTodas(Pageable pageable);
    SucursalResponseDTO obtenerPorId(Long id); 
    SucursalResponseDTO actualizar(Long id, SucursalRequestDTO dto);
    void eliminar(Long id);
    void restaurar(Long id);
}