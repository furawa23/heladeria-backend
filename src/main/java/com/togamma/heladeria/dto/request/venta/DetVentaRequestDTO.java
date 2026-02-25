package com.togamma.heladeria.dto.request.venta;

import com.togamma.heladeria.dto.DetalleTransaccionDTO;

public record DetVentaRequestDTO(
    Integer cantidad,
    Double precioUnitario,
    Long idPresentacion,
    Long idProducto
) implements DetalleTransaccionDTO {}
