package com.togamma.heladeria.dto.response.almacen;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductoResponseDTO (
    Long id,
    LocalDateTime updatedAt,
    String nombre,
    Boolean seVende,
    BigDecimal precioUnitarioVenta,
    String unidadBase,
    String categoria,
    List<RecetaItemResponseDTO> receta
) {}
