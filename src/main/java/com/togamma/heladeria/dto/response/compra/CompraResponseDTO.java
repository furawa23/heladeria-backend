package com.togamma.heladeria.dto.response.compra;

import java.time.LocalDateTime;
import java.util.List;

public record CompraResponseDTO(
    Long id,
    LocalDateTime createdAt,
    String descripcion,
    String numeroComprobante,
    Double total,
    String estado,
    String proveedor,
    List<DetCompraResponseDTO> detalles
) {}
