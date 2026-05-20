package com.togamma.heladeria.dto.response.almacen;

public record RecetaItemResponseDTO(
    Long id,
    Long idInsumo,
    String insumoNombre,
    String unidadBase,
    Integer cantidadUsada
) {}
