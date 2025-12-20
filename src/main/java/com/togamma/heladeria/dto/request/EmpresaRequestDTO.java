package com.togamma.heladeria.dto.request;

public record EmpresaRequestDTO(
    String ruc,
    String razonSocial,
    String nombreDuenio,
    String telefono
) {}