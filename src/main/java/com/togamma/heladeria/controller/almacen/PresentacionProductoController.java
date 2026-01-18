package com.togamma.heladeria.controller.almacen;

import com.togamma.heladeria.dto.request.almacen.PresentacionProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.PresentacionProdResponseDTO;
import com.togamma.heladeria.service.almacen.PresentacionProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/presentaciones-productos")
@RequiredArgsConstructor
public class PresentacionProductoController {

    private final PresentacionProductoService presentacionProductoService;

    @PostMapping
    public ResponseEntity<PresentacionProdResponseDTO> crear(@RequestBody PresentacionProdRequestDTO dto) {
        PresentacionProdResponseDTO response = presentacionProductoService.crear(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<Page<PresentacionProdResponseDTO>> listarPorProducto(@PathVariable Long idProducto, Pageable pageable) {
        Page<PresentacionProdResponseDTO> response = presentacionProductoService.listarPorProducto(idProducto, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresentacionProdResponseDTO> obtenerPorId(@PathVariable Long id) {
        PresentacionProdResponseDTO response = presentacionProductoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresentacionProdResponseDTO> actualizar(
            @PathVariable Long id, 
            @RequestBody PresentacionProdRequestDTO dto) {
        PresentacionProdResponseDTO response = presentacionProductoService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        presentacionProductoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}