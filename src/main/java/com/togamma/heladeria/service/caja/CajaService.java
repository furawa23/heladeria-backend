package com.togamma.heladeria.service.caja;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.togamma.heladeria.dto.request.caja.CajaRequestDTO;
import com.togamma.heladeria.dto.response.caja.CajaResponseDTO;

public interface CajaService {
    CajaResponseDTO abrirCaja(CajaRequestDTO dto);
    CajaResponseDTO cerrarCaja(Long id);
    CajaResponseDTO obtenerCajaAbierta();
    Page<CajaResponseDTO> listarTodas(Pageable pageable);
}