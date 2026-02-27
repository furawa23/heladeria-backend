package com.togamma.heladeria.dto.request.almacen;

import java.util.List;

public record ProductoRequestDTO(
    String nombre,
    Boolean seVende,
    Double precioUnitarioVenta,
    String unidadBase,
    Long idCategoria,
    Integer stock,
    List<RecetaItemRequestDTO> receta
) {}
