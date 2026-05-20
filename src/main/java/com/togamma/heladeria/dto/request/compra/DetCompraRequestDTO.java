package com.togamma.heladeria.dto.request.compra;

import com.togamma.heladeria.dto.DetalleTransaccionDTO;

public record DetCompraRequestDTO(
    Integer cantidad,
    Double precioUnitario,
    Long idPresentacion,
    Long idProducto
) implements DetalleTransaccionDTO {}
