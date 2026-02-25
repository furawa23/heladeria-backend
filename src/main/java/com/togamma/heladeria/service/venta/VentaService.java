package com.togamma.heladeria.service.venta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.venta.VentaRequestDTO;
import com.togamma.heladeria.dto.response.venta.VentaResponseDTO;

public interface VentaService {
    VentaResponseDTO crearRapida(VentaRequestDTO dto);
    VentaResponseDTO crearEnMesa(VentaRequestDTO dto);
    Page<VentaResponseDTO> listarTodas(Pageable pageable);
    VentaResponseDTO obtenerPorId(Long id);
    VentaResponseDTO actualizar(Long id, VentaRequestDTO dto);
    void eliminar(Long id);
    void cancelar(Long id);
    void cobrar(Long id);
}
