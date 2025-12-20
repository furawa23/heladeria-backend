package com.togamma.heladeria.dto.response;

import java.time.LocalDateTime;

public record EmpresaResponseDTO(
    Long id,
    String ruc,
    String razonSocial,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}