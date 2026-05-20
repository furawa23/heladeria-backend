package com.togamma.heladeria.dto;

public interface DetalleTransaccionDTO {
    Long idProducto();
    Long idPresentacion();
    Integer cantidad();
    Double precioUnitario();
}