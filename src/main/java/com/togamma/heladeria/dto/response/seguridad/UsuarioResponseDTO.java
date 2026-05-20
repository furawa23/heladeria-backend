package com.togamma.heladeria.dto.response.seguridad;

import java.time.LocalDateTime;

import com.togamma.heladeria.model.seguridad.Usuario;

public record UsuarioResponseDTO (
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long id,
    String username,
    String rol,
    String nombreEmpresa,   // Puedes enviar nombres en vez de objetos completos
    String nombreSucursal,
    Long idSucursal,
    Long idEmpresa
    // ¡Nota que NO incluimos el password aquí!
) {
        public static UsuarioResponseDTO mapToResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getCreatedAt(),
            usuario.getUpdatedAt(),
            usuario.getId(),
            usuario.getUsername(),
            usuario.getRol().name(),
            usuario.getEmpresa() != null ? usuario.getEmpresa().getRazonSocial() : null,
            usuario.getSucursal() != null ? usuario.getSucursal().getNombre() : null,
            usuario.getSucursal() != null ? usuario.getSucursal().getId() : null,
            usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null
        );
    }
}