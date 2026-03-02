package com.togamma.heladeria.dto.request.caja;

import com.togamma.heladeria.model.caja.TipoMovimiento;

public record MovimientoCajaRequestDTO(
    TipoMovimiento tipo,
    Double monto,
    Long idVenta,
    Long idCompra
) {}