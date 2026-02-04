package com.togamma.heladeria.dto.request.compra;

public record DetCompraRequestDTO(
    Integer cantidad,
    Double precioUnitario,
    Long idPresentacion,
    Long idProducto
) {}
