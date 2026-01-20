package com.togamma.heladeria.dto.response.compra;

public record ProveedorResponseDTO(
    Long id,
    String razonSocial,
    String ruc,
    String telefonoContacto
) {}
