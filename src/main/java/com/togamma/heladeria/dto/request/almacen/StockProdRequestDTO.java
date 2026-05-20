package com.togamma.heladeria.dto.request.almacen;

public record StockProdRequestDTO(
    Long idProducto,
    Long idSucursal,
    Integer cantidad
) {}