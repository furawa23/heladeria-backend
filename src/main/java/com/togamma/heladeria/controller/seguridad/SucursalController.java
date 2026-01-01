package com.togamma.heladeria.controller.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.togamma.heladeria.dto.request.seguridad.SucursalRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.SucursalResponseDTO;
import com.togamma.heladeria.service.seguridad.SucursalService;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    @Autowired
    private SucursalService sucursalService;

    @PostMapping
    public ResponseEntity<SucursalResponseDTO> crear(@RequestBody SucursalRequestDTO dto) {
        SucursalResponseDTO response = sucursalService.crear(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<SucursalResponseDTO>> listarTodas(Pageable pageable) {
        Page<SucursalResponseDTO> response = sucursalService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponseDTO> obtenerPorId(@PathVariable Long id) {
        SucursalResponseDTO response = sucursalService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalResponseDTO> actualizar(@PathVariable Long id, @RequestBody SucursalRequestDTO dto) {
        SucursalResponseDTO response = sucursalService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restaurar")
    public ResponseEntity<Void> restaurar(@PathVariable Long id) {
        sucursalService.restaurar(id);
        return ResponseEntity.ok().build();
    }
}