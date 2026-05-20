package com.togamma.heladeria.service.almacen;

import java.util.List;

import com.togamma.heladeria.dto.request.almacen.StockProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.StockProdResponseDTO;

public interface StockProductoService {
    StockProdResponseDTO registrarIngreso(StockProdRequestDTO dto);
    StockProdResponseDTO ajustarCantidad(Long id, Integer nuevaCantidad);
    List<StockProdResponseDTO> listarPorProducto(Long idProducto);
    StockProdResponseDTO obtenerPorProductoYSucursal(Long idProducto);
    StockProdResponseDTO obtenerPorId(Long id);
    void inicializarStock(Long idProducto, Long idSucursal);
}
