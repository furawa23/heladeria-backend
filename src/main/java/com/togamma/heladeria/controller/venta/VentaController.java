package com.togamma.heladeria.controller.venta;

import com.togamma.heladeria.dto.request.venta.VentaRequestDTO;
import com.togamma.heladeria.dto.response.venta.VentaResponseDTO;
import com.togamma.heladeria.service.venta.VentaService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponseDTO> crear(@RequestBody VentaRequestDTO dto) {
        VentaResponseDTO response;
        if (dto.idMesa() == null) {
            response = ventaService.crearRapida(dto);
        } else {
            response = ventaService.crearEnMesa(dto);
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<VentaResponseDTO>> listarTodas(Pageable pageable) {
        Page<VentaResponseDTO> response = ventaService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerPorId(@PathVariable Long id) {
        VentaResponseDTO response = ventaService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> actualizar(@PathVariable Long id, @RequestBody VentaRequestDTO dto) {
        VentaResponseDTO response = ventaService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        ventaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cobrar")
    public ResponseEntity<Void> cobrar(@PathVariable Long id) {
        ventaService.cobrar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}