package com.togamma.heladeria.service.sabor;

import java.util.List;

import com.togamma.heladeria.dto.response.sabor.SaborResponseDTO;

public interface ProductoSaborService {
    List<SaborResponseDTO> obtenerSaboresPermitidosParaProducto(Long idProducto);
    void asignarSaboresAProducto(Long idProducto, List<Long> idsSabores);
    List<Long> obtenerIdsProductosPorSabor(Long idSabor);
    void asignarProductosASabor(Long idSabor, List<Long> idsProductos);
}
