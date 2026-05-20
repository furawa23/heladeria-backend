package com.togamma.heladeria.dto.response.sabor;

import java.time.LocalDateTime;

import com.togamma.heladeria.model.sabores.Sabor;

public record SaborResponseDTO(
    LocalDateTime updatedAt,
    Long id,
    String nombre,
    Double precioAdicional
) {
    public static SaborResponseDTO mapToResponse(Sabor s) {
        return new SaborResponseDTO(
            s.getUpdatedAt(),
            s.getId(),
            s.getNombre(),
            s.getPrecioAdicional()
        );
    }
}
