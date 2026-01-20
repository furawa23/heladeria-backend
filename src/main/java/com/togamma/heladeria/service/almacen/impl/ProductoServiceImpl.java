package com.togamma.heladeria.service.almacen.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.almacen.ProductoRequestDTO;
import com.togamma.heladeria.dto.request.almacen.RecetaItemRequestDTO;
import com.togamma.heladeria.dto.response.almacen.ProductoResponseDTO;
import com.togamma.heladeria.dto.response.almacen.RecetaItemResponseDTO;
import com.togamma.heladeria.model.almacen.CategoriaProducto;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.model.almacen.RecetaItem;
import com.togamma.heladeria.repository.almacen.CategoriaProductoRepository;
import com.togamma.heladeria.repository.almacen.ProductoRepository;
import com.togamma.heladeria.service.almacen.ProductoService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaRepository;
    private final ContextService contexto;

    @Override
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {

        if (productoRepository.existsByNombreAndEmpresaId(dto.nombre(), contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("el producto ya existe");
        }

        Producto producto = new Producto();
        mapToEntity(producto, dto);
        producto.setEmpresa(contexto.getEmpresaLogueada());        

        if (Boolean.TRUE.equals(dto.seVende())) {
            actualizarReceta(producto, dto.receta());
        }

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
    public Page<ProductoResponseDTO> listarSoloInsumos(Pageable pageable) {
        return productoRepository.findByEmpresaIdAndSeVendeFalse(contexto.getEmpresaLogueada().getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarSoloVenta(Pageable pageable) {
        return productoRepository.findByEmpresaIdAndSeVendeTrue(contexto.getEmpresaLogueada().getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<ProductoResponseDTO> listarPorCategoria(Long idCategoria, Pageable pageable) {
        return productoRepository.findByCategoriaIdAndEmpresaId(idCategoria, contexto.getEmpresaLogueada().getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrada"));

        return mapToResponse(producto);
    }

    @Override
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
    
        Producto producto = productoRepository.findByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrada"));

        if (!producto.getNombre().equalsIgnoreCase(dto.nombre())) {
            if (productoRepository.existsByNombreAndEmpresaId(dto.nombre(), contexto.getEmpresaLogueada().getId())) {
                throw new RuntimeException("Ya existe un producto con ese nombre");
            }
        }
        
        mapToEntity(producto, dto);

        if (Boolean.TRUE.equals(dto.seVende())) {
            actualizarReceta(producto, dto.receta());
        } else {
            producto.getReceta().clear();
        }
    
        Producto actualizada = productoRepository.save(producto);
        return mapToResponse(actualizada);
    }
    

    @Override
    public void eliminar(Long id) {
        if (!productoRepository.existsByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("Producto no encontrada");
        }
        productoRepository.deleteById(id);
    }

    private void actualizarReceta(Producto producto, List<RecetaItemRequestDTO> recetaDTO) {
        if (producto.getReceta() == null) {
            producto.setReceta(new ArrayList<>());
        } else {
            producto.getReceta().clear();
        }

        // 1. Extraer todos los IDs de los insumos solicitados
        List<Long> insumoIds = recetaDTO.stream()
                .map(RecetaItemRequestDTO::insumoId)
                .toList();

        // 2. Buscar todos los insumos en UNA sola consulta a la BD
        List<Producto> insumosEncontrados = productoRepository.findByIdInAndEmpresaId(insumoIds, contexto.getEmpresaLogueada().getId());

        // 3. Convertir a un Map para búsqueda rápida en memoria
        // Map<ID, Producto>
        Map<Long, Producto> mapaInsumos = insumosEncontrados.stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

        // 4. Iterar y validar en memoria (sin ir a la BD)
        for (RecetaItemRequestDTO item : recetaDTO) {
            
            Producto insumo = mapaInsumos.get(item.insumoId());

            if (insumo == null) {
                throw new RuntimeException("Insumo con ID " + item.insumoId() + " no encontrado o no autorizado");
            }

            if (Boolean.TRUE.equals(insumo.getSeVende())) {
                throw new RuntimeException("El insumo '" + insumo.getNombre() + "' no es válido porque es un producto de venta");
            }

            if (insumo.getId().equals(producto.getId())) {
                throw new RuntimeException("Referencia circular: El producto no puede ser insumo de sí mismo");
            }

            if (item.cantidadUsada() <= 0) {
                throw new RuntimeException("la cantidad no puede ser menor a 0");
            }

            RecetaItem recetaItem = new RecetaItem();
            recetaItem.setProducto(producto);
            recetaItem.setInsumo(insumo);
            recetaItem.setCantidadUsada(item.cantidadUsada());

            producto.getReceta().add(recetaItem);
        }
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
                s.getInsumo().getUnidadBase(),
                s.getCantidadUsada()))
            .toList();
    }

    private void mapToEntity(Producto producto, ProductoRequestDTO dto) {

        if (Boolean.TRUE.equals(dto.seVende())) {
            if (dto.precioUnitarioVenta() == null || dto.precioUnitarioVenta() <= 0) {
                throw new RuntimeException("El precio de venta debe ser mayor a 0 para productos vendibles");
            }
        }

        producto.setNombre(dto.nombre());
        producto.setSeVende(dto.seVende());
        producto.setPrecioUnitarioVenta(dto.precioUnitarioVenta());
        producto.setUnidadBase(dto.unidadBase());
        
        if (dto.idCategoria() != null) {
            CategoriaProducto categoria = categoriaRepository
                .findByIdAndEmpresaId(dto.idCategoria(), contexto.getEmpresaLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Categoría inválida"));

            producto.setCategoria(categoria);
        }
    }
}