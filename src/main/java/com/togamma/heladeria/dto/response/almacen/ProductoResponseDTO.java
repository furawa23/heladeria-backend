package com.togamma.heladeria.dto.response.almacen;

import java.time.LocalDateTime;
import java.util.List;

public record ProductoResponseDTO (
    Long id,
    LocalDateTime updatedAt,
    String nombre,
    Boolean seVende,
    Double precioUnitarioVenta,
    String unidadBase,
    String categoria,
    List<RecetaItemResponseDTO> receta
) {}
