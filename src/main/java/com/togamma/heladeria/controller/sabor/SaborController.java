package com.togamma.heladeria.controller.sabor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.togamma.heladeria.dto.request.sabor.SaborRequestDTO;
import com.togamma.heladeria.dto.response.sabor.SaborResponseDTO;
import com.togamma.heladeria.service.sabor.ProductoSaborService;
import com.togamma.heladeria.service.sabor.SaborService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sabores")
@RequiredArgsConstructor
public class SaborController {

    private final SaborService saborService;
    private final ProductoSaborService productoSaborService;

    @PostMapping
    public ResponseEntity<SaborResponseDTO> crear(@RequestBody SaborRequestDTO dto) {
        SaborResponseDTO response = saborService.crear(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<SaborResponseDTO>> listarTodas(Pageable pageable) {
        Page<SaborResponseDTO> response = saborService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaborResponseDTO> obtenerPorId(@PathVariable Long id) {
        SaborResponseDTO response = saborService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaborResponseDTO> actualizar(@PathVariable Long id, @RequestBody SaborRequestDTO dto) {
        SaborResponseDTO response = saborService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        saborService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idSabor}/productos")
    public ResponseEntity<List<Long>> obtenerIdsProductosPorSabor(@PathVariable Long idSabor) {
        List<Long> idsProductos = productoSaborService.obtenerIdsProductosPorSabor(idSabor);
        return ResponseEntity.ok(idsProductos);
    }

    @PostMapping("/{idSabor}/productos")
    public ResponseEntity<Void> asignarProductosASabor(
            @PathVariable Long idSabor, 
            @RequestBody List<Long> idsProductos) {
            
        productoSaborService.asignarProductosASabor(idSabor, idsProductos);
        return ResponseEntity.ok().build();
    }
}
