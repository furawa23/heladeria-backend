package com.togamma.heladeria.service.seguridad;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.seguridad.RegisterRequestDTO;
import com.togamma.heladeria.dto.request.seguridad.UsuarioRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.UsuarioResponseDTO;

public interface UsuarioService {
    UsuarioResponseDTO crearDesdeSuperadmin(UsuarioRequestDTO dto);
    UsuarioResponseDTO crearDesdeEmpresa(RegisterRequestDTO dto);
    Page<UsuarioResponseDTO> listarTodos(Pageable pageable);
    Page<UsuarioResponseDTO> listarPorSucursal(Long idSucursal, Pageable pageable);
    Page<UsuarioResponseDTO> listarPorEmpresa(Long idEmpresa, Pageable pageable);
    UsuarioResponseDTO obtenerPorId(Long id); 
    UsuarioResponseDTO actualizar(Long id, RegisterRequestDTO dto);
    void eliminar(Long id);
    void restaurar(Long id);
}