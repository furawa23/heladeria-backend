package com.togamma.heladeria.dto.request.venta;

import java.util.List;

import com.togamma.heladeria.dto.DetalleTransaccionDTO;

public record DetVentaRequestDTO(
    Integer cantidad,
    Double precioUnitario,
    Long idPresentacion,
    Long idProducto,
    List<Long> idsSabores
) implements DetalleTransaccionDTO {}
