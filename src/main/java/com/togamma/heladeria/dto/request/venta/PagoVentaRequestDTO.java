package com.togamma.heladeria.dto.request.venta;

import com.togamma.heladeria.model.venta.TipoMetodoPago;

public record PagoVentaRequestDTO(
    TipoMetodoPago metodoPago,
    Double monto
) {}