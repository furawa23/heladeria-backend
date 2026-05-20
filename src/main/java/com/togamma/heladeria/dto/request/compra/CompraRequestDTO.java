package com.togamma.heladeria.dto.request.compra;

import java.util.List;

public record CompraRequestDTO(   
    String descripcion,
    String numeroComprobante,
    String estado,
    Long idProveedor,
    List<DetCompraRequestDTO> detalles
) 
{}
