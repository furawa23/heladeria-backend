package com.togamma.heladeria.controller.caja;

import com.togamma.heladeria.dto.request.caja.MovimientoCajaRequestDTO;
import com.togamma.heladeria.dto.response.caja.MovimientoCajaResponseDTO;
import com.togamma.heladeria.service.caja.MovimientoCajaService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movimientos-caja")
@RequiredArgsConstructor
public class MovimientoCajaController {

    private final MovimientoCajaService movimientoCajaService;

    @PostMapping
    public ResponseEntity<MovimientoCajaResponseDTO> registrarMovimiento(@RequestBody MovimientoCajaRequestDTO dto) {
        MovimientoCajaResponseDTO response = movimientoCajaService.registrarMovimiento(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/caja/{idCaja}")
    public ResponseEntity<Page<MovimientoCajaResponseDTO>> listarPorCaja(@PathVariable Long idCaja, Pageable pageable) {
        Page<MovimientoCajaResponseDTO> response = movimientoCajaService.listarPorCaja(idCaja, pageable);
        return ResponseEntity.ok(response);
    }
}