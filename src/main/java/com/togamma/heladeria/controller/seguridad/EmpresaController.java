package com.togamma.heladeria.controller.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.togamma.heladeria.dto.request.seguridad.EmpresaRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.EmpresaResponseDTO;
import com.togamma.heladeria.service.seguridad.EmpresaService;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> crear(@RequestBody EmpresaRequestDTO dto) {
        EmpresaResponseDTO response = empresaService.crear(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<EmpresaResponseDTO>> listarTodas(Pageable pageable) {
        Page<EmpresaResponseDTO> response = empresaService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> obtenerPorId(@PathVariable Long id) {
        EmpresaResponseDTO response = empresaService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> actualizar(@PathVariable Long id, @RequestBody EmpresaRequestDTO dto) {
        EmpresaResponseDTO response = empresaService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        empresaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restaurar")
    public ResponseEntity<Void> restaurar(@PathVariable Long id) {
        empresaService.restaurar(id);
        return ResponseEntity.ok().build();
    }
}
