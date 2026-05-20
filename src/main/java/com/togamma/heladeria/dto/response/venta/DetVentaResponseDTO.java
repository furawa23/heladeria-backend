package com.togamma.heladeria.dto.response.venta;

import java.util.List;

public record DetVentaResponseDTO(
    Long id,
    Long idProducto,
    String nombreProducto,
    Long idPresentacion,
    String nombrePresentacion,
    Integer cantidad,
    Double precioUnitario,
    Double subtotal,
    List<String> sabores
) {
}
