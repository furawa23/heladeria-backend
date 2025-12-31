package com.togamma.heladeria.dto.response.seguridad;

import java.time.LocalDateTime;
import java.util.List;

public record EmpresaResponseDTO (
    Long id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    String ruc,
    String razonSocial,
    String nombreDuenio,
    String telefono,
    List<SucursalResponseDTO> sucursales
) {}
