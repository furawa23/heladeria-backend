package com.togamma.heladeria.dto.response.almacen;

import java.time.LocalDateTime;

public record PresentacionProdResponseDTO(
    Long id,
    LocalDateTime createdAt,
    String nombre,
    Integer factor,
    String nombreProd
) {}
