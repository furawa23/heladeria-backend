package com.togamma.heladeria.dto.request.seguridad;

public record UsuarioRequestDTO ( String username, String password, String rol, Long idSucursal ) {}