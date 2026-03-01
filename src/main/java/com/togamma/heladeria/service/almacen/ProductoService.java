package com.togamma.heladeria.service.almacen;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.togamma.heladeria.dto.request.almacen.ProductoRequestDTO;
import com.togamma.heladeria.dto.response.almacen.ProductoResponseDTO;

public interface ProductoService {
    ProductoResponseDTO crear(ProductoRequestDTO dto);
    Page<ProductoResponseDTO> listarTodas(Pageable pageable);
    Page<ProductoResponseDTO> listarPorCategoria(Long idCategoria, Pageable pageable);
    Page<ProductoResponseDTO> listarSoloInsumos(Pageable pageable);
    Page<ProductoResponseDTO> listarSoloVenta(Pageable pageable);
    ProductoResponseDTO obtenerPorId(Long id); 
    ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto);
    List<ProductoResponseDTO> listarProductosDisponiblesParaVenta();
    void eliminar(Long id);
}
