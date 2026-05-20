package com.togamma.heladeria.service.caja;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.togamma.heladeria.dto.request.caja.MovimientoCajaRequestDTO;
import com.togamma.heladeria.dto.response.caja.MovimientoCajaResponseDTO;

public interface MovimientoCajaService {
    MovimientoCajaResponseDTO registrarMovimiento(MovimientoCajaRequestDTO dto);
    Page<MovimientoCajaResponseDTO> listarPorCaja(Long idCaja, Pageable pageable);
}