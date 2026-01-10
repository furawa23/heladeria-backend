package com.togamma.heladeria.dto.response.seguridad;

import java.time.LocalDateTime;

public record UsuarioResponseDTO (
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long id,
    String username,
    String rol,
    String nombreEmpresa,   // Puedes enviar nombres en vez de objetos completos
    String nombreSucursal
    // ¡Nota que NO incluimos el password aquí!
) {}