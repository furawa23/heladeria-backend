package com.togamma.heladeria.controller.almacen;

import com.togamma.heladeria.dto.request.almacen.ProductoRequestDTO;
import com.togamma.heladeria.dto.response.almacen.ProductoResponseDTO;
import com.togamma.heladeria.dto.response.sabor.SaborResponseDTO;
import com.togamma.heladeria.service.almacen.ProductoService;
import com.togamma.heladeria.service.sabor.ProductoSaborService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final ProductoSaborService productoSaborService;

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO response = productoService.crear(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<ProductoResponseDTO>> listarTodas(Pageable pageable) {
        Page<ProductoResponseDTO> response = productoService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/insumos")
    public ResponseEntity<Page<ProductoResponseDTO>> listarInsumos(Pageable pageable) {
        Page<ProductoResponseDTO> response = productoService.listarSoloInsumos(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/venta")
    public ResponseEntity<Page<ProductoResponseDTO>> listarProductos(Pageable pageable) {
        Page<ProductoResponseDTO> response = productoService.listarSoloVenta(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sin-receta")
    public ResponseEntity<Page<ProductoResponseDTO>> listarSinReceta(Pageable pageable) {
        Page<ProductoResponseDTO> response = productoService.listarSinReceta(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/disponible-venta")
    public ResponseEntity<List<ProductoResponseDTO>> listarDisponiblesParaVenta() {
        List<ProductoResponseDTO> response = productoService.listarProductosDisponiblesParaVenta();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categoria/{idCat}")
    public ResponseEntity<Page<ProductoResponseDTO>> listarPorCategoria(@PathVariable Long idCat, Pageable pageable) {
        Page<ProductoResponseDTO> response = productoService.listarPorCategoria(idCat, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        ProductoResponseDTO response = productoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id, @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO response = productoService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    //----------- sabores -----------

    @GetMapping("/{idProducto}/sabores")
    public ResponseEntity<List<SaborResponseDTO>> obtenerSaboresPermitidos(@PathVariable Long idProducto) {
        List<SaborResponseDTO> sabores = productoSaborService.obtenerSaboresPermitidosParaProducto(idProducto);
        return ResponseEntity.ok(sabores);
    }

    @PostMapping("/{idProducto}/sabores")
    public ResponseEntity<Void> asignarSabores(
            @PathVariable Long idProducto, 
            @RequestBody List<Long> idsSabores) {
            
        productoSaborService.asignarSaboresAProducto(idProducto, idsSabores);
        return ResponseEntity.ok().build();
    }
}