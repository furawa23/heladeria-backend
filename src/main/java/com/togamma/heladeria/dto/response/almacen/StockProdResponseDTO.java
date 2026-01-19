package com.togamma.heladeria.dto.response.almacen;

import java.time.LocalDateTime;

public record StockProdResponseDTO(
    Long id,
    LocalDateTime updatedAt,
    String nombreProducto,
    String unidadMedida,
    String nombreSucursal,
    Integer cantidadActual
) {}
