package com.togamma.heladeria.dto.response.compra;

public record DetCompraResponseDTO(
    Long id,
    Long idProducto,
    String nombreProducto,
    Long idPresentacion,
    String nombrePresentacion,
    Integer cantidad,
    Double precioUnitario,
    Double subtotal
) {

}
