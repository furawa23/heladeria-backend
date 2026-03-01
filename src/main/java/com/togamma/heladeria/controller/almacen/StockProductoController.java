package com.togamma.heladeria.controller.almacen;

import com.togamma.heladeria.dto.request.almacen.StockProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.StockProdResponseDTO;
import com.togamma.heladeria.service.almacen.StockProductoService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock-productos")
@RequiredArgsConstructor
public class StockProductoController {

    private final StockProductoService stockProductoService;

    @PostMapping
    public ResponseEntity<StockProdResponseDTO> registrarIngreso(@RequestBody StockProdRequestDTO dto) {
        StockProdResponseDTO response = stockProductoService.registrarIngreso(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<StockProdResponseDTO>> listarPorProducto(@PathVariable Long idProducto) {
        List<StockProdResponseDTO> response = stockProductoService.listarPorProducto(idProducto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/producto/{idProducto}/sucursal")
    public ResponseEntity<StockProdResponseDTO> obtenerStockPorSucursal(
            @PathVariable Long idProducto) {
        
        StockProdResponseDTO response = stockProductoService.obtenerPorProductoYSucursal(idProducto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockProdResponseDTO> obtenerPorId(@PathVariable Long id) {
        StockProdResponseDTO response = stockProductoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ajuste")
    public ResponseEntity<StockProdResponseDTO> ajustarCantidad(
            @PathVariable Long id, 
            @RequestParam Integer nuevaCantidad) {
        StockProdResponseDTO response = stockProductoService.ajustarCantidad(id, nuevaCantidad);
        return ResponseEntity.ok(response);
    }
}