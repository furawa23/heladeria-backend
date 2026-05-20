package com.togamma.heladeria.service.sabor.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.response.sabor.SaborResponseDTO;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.model.sabores.ProductoSabor;
import com.togamma.heladeria.model.sabores.Sabor;
import com.togamma.heladeria.repository.almacen.ProductoRepository;
import com.togamma.heladeria.repository.sabor.ProductoSaborRepository;
import com.togamma.heladeria.repository.sabor.SaborRepository;
import com.togamma.heladeria.service.sabor.ProductoSaborService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoSaborServiceImpl implements ProductoSaborService {

    private final ProductoSaborRepository productoSaborRepository;  
    private final ProductoRepository productoRepository;
    private final SaborRepository saborRepository;

    // Obtener los sabores permitidos para un producto específico
    public List<SaborResponseDTO> obtenerSaboresPermitidosParaProducto(Long idProducto) {
        List<ProductoSabor> relaciones = productoSaborRepository.findByProductoId(idProducto);
        // Extraemos solo la entidad Sabor de la relación
        return relaciones.stream()
                         .map(ProductoSabor::getSabor)
                         .filter(sabor -> sabor.getDeletedAt() == null) // Solo sabores activos
                         .map(SaborResponseDTO::mapToResponse) // Map to SaborResponseDTO
                         .collect(Collectors.toList());
    }

    @Transactional
    public void asignarSaboresAProducto(Long idProducto, List<Long> idsSabores) {
        Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        productoSaborRepository.deleteByProductoId(idProducto);

        for (Long idSabor : idsSabores) {
            Sabor sabor = saborRepository.findById(idSabor)
                .orElseThrow(() -> new RuntimeException("Sabor no encontrado"));
                
            ProductoSabor relacion = new ProductoSabor();
            relacion.setProducto(producto);
            relacion.setSabor(sabor);
            productoSaborRepository.save(relacion);
        }
    }

    // 1. Para cargar el MultiSelect cuando le das a "Editar" Sabor
    public List<Long> obtenerIdsProductosPorSabor(Long idSabor) {
        List<ProductoSabor> relaciones = productoSaborRepository.findBySaborId(idSabor);
        return relaciones.stream()
                         .map(relacion -> relacion.getProducto().getId())
                         .collect(Collectors.toList());
    }

    // 2. Para guardar cuando le das a "Guardar" en el modal de Sabores
    @Transactional
    public void asignarProductosASabor(Long idSabor, List<Long> idsProductos) {
        Sabor sabor = saborRepository.findById(idSabor)
            .orElseThrow(() -> new RuntimeException("Sabor no encontrado"));

        productoSaborRepository.deleteBySaborId(idSabor);

        for (Long idProducto : idsProductos) {
            Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
            ProductoSabor relacion = new ProductoSabor();
            relacion.setProducto(producto);
            relacion.setSabor(sabor);
            productoSaborRepository.save(relacion);
        }
    }

}