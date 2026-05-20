package com.togamma.heladeria.dto.response.venta;

import java.time.LocalDateTime;
import java.util.List;

import com.togamma.heladeria.model.venta.EstadoVenta;

public record VentaResponseDTO(
    Long id,
    LocalDateTime createdAt,
    String numeroComprobante,
    Double total,
    EstadoVenta estado,
    Integer numeroMesa,
    List<DetVentaResponseDTO> detalles
) {}
