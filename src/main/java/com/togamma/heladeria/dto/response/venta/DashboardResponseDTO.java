package com.togamma.heladeria.dto.response.venta;

import java.util.List;

public record DashboardResponseDTO(
    Double totalVentas,
    Long cantidadVentas,
    Double ticketPromedio,
    List<VentasPorDiaDTO> ventasPorDia,
    List<ProductoMasVendidoDTO> productosMasVendidos,
    List<VentasPorMesaDTO> ventasPorMesa
) {
    public record VentasPorDiaDTO(String fecha, Double total) {}
    public record ProductoMasVendidoDTO(String producto, Long cantidad) {}
    public record VentasPorMesaDTO(String mesa, Double total) {}
}
