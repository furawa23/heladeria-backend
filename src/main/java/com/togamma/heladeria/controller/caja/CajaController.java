package com.togamma.heladeria.controller.caja;

import com.togamma.heladeria.dto.request.caja.CajaRequestDTO;
import com.togamma.heladeria.dto.response.caja.CajaResponseDTO;
import com.togamma.heladeria.service.caja.CajaService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cajas")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService cajaService;

    @PostMapping("/abrir")
    public ResponseEntity<CajaResponseDTO> abrirCaja(@RequestBody CajaRequestDTO dto) {
        CajaResponseDTO response = cajaService.abrirCaja(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<CajaResponseDTO> cerrarCaja(@PathVariable Long id) {
        CajaResponseDTO response = cajaService.cerrarCaja(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/abierta")
    public ResponseEntity<CajaResponseDTO> obtenerCajaAbierta() {
        CajaResponseDTO response = cajaService.obtenerCajaAbierta();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CajaResponseDTO>> listarTodas(Pageable pageable) {
        Page<CajaResponseDTO> response = cajaService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }
}