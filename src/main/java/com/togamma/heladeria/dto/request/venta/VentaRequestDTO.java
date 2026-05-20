package com.togamma.heladeria.dto.request.venta;

import java.util.List;

public record VentaRequestDTO(
    String numeroComprobante,
    String estado,
    Long idMesa,
    List<DetVentaRequestDTO> detalles
)
{}
