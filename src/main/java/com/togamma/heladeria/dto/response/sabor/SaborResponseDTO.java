package com.togamma.heladeria.dto.response.sabor;

import java.time.LocalDateTime;

import com.togamma.heladeria.model.sabores.Sabor;

public record SaborResponseDTO(
    LocalDateTime getUpdatedAt,
    String nombre,
    Double precioAdicional
) {
    public static SaborResponseDTO mapToResponse(Sabor s) {
        return new SaborResponseDTO(
            s.getUpdatedAt(),
            s.getNombre(),
            s.getPrecioAdicional()
        );
    }
}
