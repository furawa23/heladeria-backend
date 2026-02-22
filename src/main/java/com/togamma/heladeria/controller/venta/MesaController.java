package com.togamma.heladeria.controller.venta;

import com.togamma.heladeria.dto.request.venta.MesaRequestDTO;
import com.togamma.heladeria.dto.response.venta.MesaResponseDTO;
import com.togamma.heladeria.service.venta.MesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @PostMapping
    public ResponseEntity<MesaResponseDTO> crear(@RequestBody MesaRequestDTO dto) {
        MesaResponseDTO response = mesaService.crear(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<MesaResponseDTO>> listarTodas(Pageable pageable) {
        Page<MesaResponseDTO> response = mesaService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> obtenerPorId(@PathVariable Long id) {
        MesaResponseDTO response = mesaService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> actualizar(@PathVariable Long id, @RequestBody MesaRequestDTO dto) {
        MesaResponseDTO response = mesaService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}