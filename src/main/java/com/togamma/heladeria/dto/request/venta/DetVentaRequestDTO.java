package com.togamma.heladeria.dto.request.venta;

public record DetVentaRequestDTO(
    Integer cantidad,
    Double precioUnitario,
    Long idPresentacion,
    Long idProducto
) {}
