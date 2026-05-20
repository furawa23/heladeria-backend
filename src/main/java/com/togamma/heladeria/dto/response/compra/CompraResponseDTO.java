package com.togamma.heladeria.dto.response.compra;

import java.time.LocalDateTime;
import java.util.List;

import com.togamma.heladeria.model.compra.EstadoCompra;

public record CompraResponseDTO(
    Long id,
    LocalDateTime createdAt,
    String descripcion,
    String numeroComprobante,
    Double total,
    EstadoCompra estado,
    String proveedor,
    List<DetCompraResponseDTO> detalles
) {}
