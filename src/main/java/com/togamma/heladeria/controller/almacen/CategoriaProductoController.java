package com.togamma.heladeria.controller.almacen;

import com.togamma.heladeria.dto.request.almacen.CategoriaProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.CategoriaProdResponseDTO;
import com.togamma.heladeria.service.almacen.CategoriaProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias-productos")
@RequiredArgsConstructor
public class CategoriaProductoController {

    private final CategoriaProductoService categoriaProductoService;

    @PostMapping
    public ResponseEntity<CategoriaProdResponseDTO> crear(@RequestBody CategoriaProdRequestDTO dto) {
        CategoriaProdResponseDTO response = categoriaProductoService.crear(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CategoriaProdResponseDTO>> listarTodas(Pageable pageable) {
        Page<CategoriaProdResponseDTO> response = categoriaProductoService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProdResponseDTO> obtenerPorId(@PathVariable Long id) {
        CategoriaProdResponseDTO response = categoriaProductoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProdResponseDTO> actualizar(@PathVariable Long id, @RequestBody CategoriaProdRequestDTO dto) {
        CategoriaProdResponseDTO response = categoriaProductoService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaProductoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}