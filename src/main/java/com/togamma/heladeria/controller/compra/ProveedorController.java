package com.togamma.heladeria.controller.compra;

import com.togamma.heladeria.dto.request.compra.ProveedorRequestDTO;
import com.togamma.heladeria.dto.response.compra.ProveedorResponseDTO;
import com.togamma.heladeria.service.compra.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<ProveedorResponseDTO> crear(@RequestBody ProveedorRequestDTO dto) {
        ProveedorResponseDTO response = proveedorService.crear(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ProveedorResponseDTO>> listarTodas(Pageable pageable) {
        Page<ProveedorResponseDTO> response = proveedorService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> obtenerPorId(@PathVariable Long id) {
        ProveedorResponseDTO response = proveedorService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> actualizar(@PathVariable Long id, @RequestBody ProveedorRequestDTO dto) {
        ProveedorResponseDTO response = proveedorService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}