package com.togamma.heladeria.dto.request.caja;

import com.togamma.heladeria.model.caja.TipoMovimiento;
import com.togamma.heladeria.model.venta.TipoMetodoPago;

public record MovimientoCajaRequestDTO(
    TipoMovimiento tipo,
    Double monto,
    Long idVenta,
    Long idCompra,
    TipoMetodoPago metodoPago
) {}