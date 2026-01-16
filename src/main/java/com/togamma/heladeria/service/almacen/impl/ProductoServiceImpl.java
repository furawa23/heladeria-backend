package com.togamma.heladeria.service.almacen.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.almacen.ProductoRequestDTO;
import com.togamma.heladeria.dto.response.almacen.ProductoResponseDTO;
import com.togamma.heladeria.dto.response.almacen.RecetaItemResponseDTO;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.model.almacen.RecetaItem;
import com.togamma.heladeria.repository.almacen.ProductoRepository;
import com.togamma.heladeria.service.almacen.ProductoService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ContextService contexto;

    @Override
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {

        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setEmpresa(contexto.getEmpresaLogueada());

        Producto guardada = productoRepository.save(producto);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarTodas(Pageable page) {
        return productoRepository.findByEmpresaId(contexto.getEmpresaLogueada().getId(), page)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria de Producto no encontrada"));

        return mapToResponse(producto);
    }

    @Override
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
    
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria de Producto no encontrada"));

        if (!producto.getEmpresa().getId().equals(contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("No tienes permiso para modificar esta categoría");
        }

        producto.setNombre(dto.nombre());
    
        Producto actualizada = productoRepository.save(producto);
    
        return mapToResponse(actualizada);
    }
    

    @Override
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Categoria de Producto no encontrada");
        }
        productoRepository.deleteById(id);
    }

    private ProductoResponseDTO mapToResponse(Producto s) {
        return new ProductoResponseDTO (
            s.getId(),
            s.getUpdatedAt(),
            s.getNombre(),
            s.getSeVende(),
            s.getPrecioUnitarioVenta(),
            s.getUnidadBase(),
            s.getCategoria().getNombre(),
            mapItemsReceta(s.getReceta())
        );
    }

    private List<RecetaItemResponseDTO> mapItemsReceta(List<RecetaItem> item) {
        if (item == null) return List.of();
        return item.stream()
            .map(s -> new RecetaItemResponseDTO(
                s.getId(),
                s.getInsumo().getId(),
                s.getInsumo().getNombre(),
                s.getProducto().getUnidadBase(),
                s.getCantidadUsada()))
            .toList();
    }
}