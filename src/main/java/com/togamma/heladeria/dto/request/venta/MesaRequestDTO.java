package com.togamma.heladeria.dto.request.venta;

import com.togamma.heladeria.dto.request.seguridad.SucursalRequestDTO;

public record MesaRequestDTO(Integer numero, SucursalRequestDTO sucursal) {}
