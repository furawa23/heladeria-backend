package com.togamma.heladeria.dto.response.caja;

import java.time.LocalDateTime;
import com.togamma.heladeria.model.caja.TipoMovimiento;
import com.togamma.heladeria.model.venta.TipoMetodoPago;

public record MovimientoCajaResponseDTO(
    Long id,
    TipoMovimiento tipo,
    Double monto,
    Long idVenta,
    Long idCompra,
    TipoMetodoPago metodoPago,
    LocalDateTime fechaCreacion
) {}