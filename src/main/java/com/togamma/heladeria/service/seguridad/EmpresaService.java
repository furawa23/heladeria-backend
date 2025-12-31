package com.togamma.heladeria.service.seguridad;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.seguridad.EmpresaRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.EmpresaResponseDTO;

public interface EmpresaService {
    EmpresaResponseDTO crear(EmpresaRequestDTO dto);
    EmpresaResponseDTO obtenerPorId(Long id);
    Page<EmpresaResponseDTO> listarTodas(Pageable page);
    EmpresaResponseDTO actualizar(Long id, EmpresaRequestDTO dto);
    void eliminar(Long id);
    void restaurar(Long id);
}