package com.togamma.heladeria.dto.request.venta;

import java.util.List;

public record CobrarVentaRequestDTO(
    List<PagoVentaRequestDTO> pagos
) {}