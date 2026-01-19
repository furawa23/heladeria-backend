package com.togamma.heladeria.service.almacen;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.almacen.StockProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.StockProdResponseDTO;

public interface StockProductoService {
    StockProdResponseDTO registrarIngreso(StockProdRequestDTO dto);
    StockProdResponseDTO ajustarCantidad(Long id, Integer nuevaCantidad);
    Page<StockProdResponseDTO> listarPorProducto(Long idProducto, Pageable pageable);
    StockProdResponseDTO obtenerPorId(Long id);
}
