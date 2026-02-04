package com.togamma.heladeria.controller.compra;

import com.togamma.heladeria.dto.request.compra.CompraRequestDTO;
import com.togamma.heladeria.dto.response.compra.CompraResponseDTO;
import com.togamma.heladeria.service.compra.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<CompraResponseDTO> crear(@RequestBody CompraRequestDTO dto) {
        CompraResponseDTO response = compraService.crear(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CompraResponseDTO>> listarTodas(Pageable pageable) {
        Page<CompraResponseDTO> response = compraService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> obtenerPorId(@PathVariable Long id) {
        CompraResponseDTO response = compraService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> actualizar(@PathVariable Long id, @RequestBody CompraRequestDTO dto) {
        CompraResponseDTO response = compraService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        compraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}