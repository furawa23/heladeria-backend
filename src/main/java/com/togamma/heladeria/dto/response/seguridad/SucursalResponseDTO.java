package com.togamma.heladeria.dto.response.seguridad;

import java.time.LocalDateTime;

public record SucursalResponseDTO (
    Long id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    String nombre,
    String direccion,
    String nombreEmpresa
) {}
