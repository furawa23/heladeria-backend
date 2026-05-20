package com.togamma.heladeria.service.almacen;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.almacen.PresentacionProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.PresentacionProdResponseDTO;

public interface PresentacionProductoService {
    PresentacionProdResponseDTO crear(PresentacionProdRequestDTO dto);
    Page<PresentacionProdResponseDTO> listarPorProducto(Long idProducto, Pageable pageable);
    PresentacionProdResponseDTO obtenerPorId(Long id); 
    PresentacionProdResponseDTO actualizar(Long id, PresentacionProdRequestDTO dto);
    void eliminar(Long id);
}
