package com.togamma.heladeria.dto.request.almacen;

import java.math.BigDecimal;
import java.util.List;

public record ProductoRequestDTO(
    String nombre,
    Boolean seVende,
    BigDecimal precioUnitarioVenta,
    String unidadBase,
    Long idCategoria,
    List<RecetaItemRequestDTO> receta
) {}
