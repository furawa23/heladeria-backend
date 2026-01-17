package com.togamma.heladeria.dto.response.almacen;

import java.time.LocalDateTime;

public record CategoriaProdResponseDTO(
    Long id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String nombre
) {}
