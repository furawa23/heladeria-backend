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
import com.togamma.heladeria.dto.request.almacen.StockProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.ProductoResponseDTO;
import com.togamma.heladeria.dto.response.almacen.RecetaItemResponseDTO;
import com.togamma.heladeria.dto.response.almacen.StockProdResponseDTO;
import com.togamma.heladeria.model.almacen.CategoriaProducto;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.model.almacen.RecetaItem;
import com.togamma.heladeria.model.seguridad.Sucursal;
import com.togamma.heladeria.repository.almacen.CategoriaProductoRepository;
import com.togamma.heladeria.repository.almacen.ProductoRepository;
import com.togamma.heladeria.service.almacen.ProductoService;
import com.togamma.heladeria.service.almacen.StockProductoService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaRepository;
    private final ContextService contexto;
    private final StockProductoService stockProductoService;

    @Override
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {

        if (productoRepository.existsByNombreAndEmpresaId(dto.nombre(), contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("El producto ya existe");
        }

        Producto producto = new Producto();
        mapToEntity(producto, dto);
        producto.setEmpresa(contexto.getEmpresaLogueada());        

        boolean tieneReceta = dto.receta() != null && !dto.receta().isEmpty();

        if (tieneReceta) {
            // Si tiene receta, bloqueamos que intenten enviarle stock inicial
            if (dto.stock() != null && dto.stock() > 0) {
                throw new RuntimeException("Un producto con receta no puede tener stock inicial físico.");
            }
            actualizarReceta(producto, dto.receta());
        }

        Producto guardada = productoRepository.save(producto);

        // --- REGISTRO DE STOCK INICIAL (ROLES + RECETA) ---
        Integer stockInicial = 0;
        Sucursal sucursal = contexto.getSucursalLogueadaOrNull();

        // Solo inicializamos/registramos stock físico si ES EMPLEADO y NO TIENE RECETA
        if (sucursal != null && !tieneReceta) {
            Long idSucursal = sucursal.getId(); 
            
            if (dto.stock() != null && dto.stock() > 0) {
                StockProdRequestDTO stockDto = new StockProdRequestDTO(
                    guardada.getId(), idSucursal, dto.stock()
                );
                StockProdResponseDTO stockRes = stockProductoService.registrarIngreso(stockDto);
                stockInicial = stockRes.cantidadActual();
            } else {
                stockProductoService.inicializarStock(guardada.getId(), idSucursal);
                stockInicial = 0;
            }
        }

        return mapToResponse(guardada, stockInicial);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarTodas(Pageable page) {
        return productoRepository.findByEmpresaId(contexto.getEmpresaLogueada().getId(), page)
                .map(this::mapToResponseParaListados);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarSoloInsumos(Pageable pageable) {
        return productoRepository.findByEmpresaIdAndSeVendeFalse(contexto.getEmpresaLogueada().getId(), pageable)
                .map(this::mapToResponseParaListados);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarSoloVenta(Pageable pageable) {
        return productoRepository.findByEmpresaIdAndSeVendeTrue(contexto.getEmpresaLogueada().getId(), pageable)
                .map(this::mapToResponseParaListados);
    }

    @Override
    public Page<ProductoResponseDTO> listarPorCategoria(Long idCategoria, Pageable pageable) {
        return productoRepository.findByCategoriaIdAndEmpresaId(idCategoria, contexto.getEmpresaLogueada().getId(), pageable)
                .map(this::mapToResponseParaListados);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarSinReceta(Pageable pageable) {
        return productoRepository.findByEmpresaIdAndRecetaIsEmpty(contexto.getEmpresaLogueada().getId(), pageable)
                .map(this::mapToResponseParaListados);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return mapToResponseParaListados(producto);
    }

    @Override
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
    
        Producto producto = productoRepository.findByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!producto.getNombre().equalsIgnoreCase(dto.nombre())) {
            if (productoRepository.existsByNombreAndEmpresaIdAndIdNot(dto.nombre(), contexto.getEmpresaLogueada().getId(), id)) {
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
        
        return mapToResponseParaListados(actualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductosDisponiblesParaVenta() {
        Sucursal sucursal = contexto.getSucursalLogueadaOrNull();
        if (sucursal == null) {
            throw new RuntimeException("Debe estar logueado en una sucursal para consultar disponibilidad de venta");
        }

        // Obtenemos todos los productos marcados para venta de la empresa
        List<Producto> productosVenta = productoRepository.findByEmpresaIdAndSeVendeTrue(contexto.getEmpresaLogueada().getId());

        List<ProductoResponseDTO> disponibles = new ArrayList<>();

        for (Producto p : productosVenta) {
            int stockDisponible = calcularStockReal(p);

            // Si se puede vender al menos 1 unidad, lo agregamos a la respuesta
            if (stockDisponible > 0) {
                // Usamos tu método existente mapToResponse, inyectando el stock calculado
                disponibles.add(mapToResponse(p, stockDisponible));
            }
        }

        return disponibles;
    }
    
    @Override
    public void eliminar(Long id) {
        if (!productoRepository.existsByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    private int calcularStockReal(Producto producto) {
        if (producto.getReceta() == null || producto.getReceta().isEmpty()) {
            // Producto directo sin receta: buscar su stock físico
            StockProdResponseDTO stockPropio = stockProductoService.obtenerPorProductoYSucursal(producto.getId());
            return stockPropio.cantidadActual() != null ? stockPropio.cantidadActual() : 0;
        } else {
            // Producto con receta: calcular el límite basado en los insumos
            int maxUnidadesPosibles = Integer.MAX_VALUE;

            for (RecetaItem item : producto.getReceta()) {
                StockProdResponseDTO stockInsumo = stockProductoService.obtenerPorProductoYSucursal(item.getInsumo().getId());
                int cantidadInsumoDisponible = stockInsumo.cantidadActual() != null ? stockInsumo.cantidadActual() : 0;

                // Si no hay suficiente ni para una unidad, cortamos el cálculo
                if (cantidadInsumoDisponible < item.getCantidadUsada()) {
                    return 0;
                }

                // ¿Para cuántas unidades finales alcanza este insumo?
                int unidadesConEsteInsumo = cantidadInsumoDisponible / item.getCantidadUsada();

                // El "cuello de botella" define el stock máximo real
                if (unidadesConEsteInsumo < maxUnidadesPosibles) {
                    maxUnidadesPosibles = unidadesConEsteInsumo;
                }
            }

            return maxUnidadesPosibles == Integer.MAX_VALUE ? 0 : maxUnidadesPosibles;
        }
    }

    private void actualizarReceta(Producto producto, List<RecetaItemRequestDTO> recetaDTO) {
        if (producto.getReceta() == null) {
            producto.setReceta(new ArrayList<>());
        } else {
            producto.getReceta().clear();
        }

        List<Long> insumoIds = recetaDTO.stream()
                .map(RecetaItemRequestDTO::insumoId)
                .toList();

        List<Producto> insumosEncontrados = productoRepository.findByIdInAndEmpresaId(insumoIds, contexto.getEmpresaLogueada().getId());

        Map<Long, Producto> mapaInsumos = insumosEncontrados.stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

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
                throw new RuntimeException("La cantidad no puede ser menor a 0");
            }

            RecetaItem recetaItem = new RecetaItem();
            recetaItem.setProducto(producto);
            recetaItem.setInsumo(insumo);
            recetaItem.setCantidadUsada(item.cantidadUsada());

            producto.getReceta().add(recetaItem);
        }
    }
    
    private ProductoResponseDTO mapToResponse(Producto s, Integer stock) {
        return new ProductoResponseDTO (
            s.getId(),
            s.getUpdatedAt(),
            s.getNombre(),
            s.getSeVende(),
            s.getPrecioUnitarioVenta(),
            s.getUnidadBase(),
            stock != null ? stock : 0, 
            s.getCategoria().getNombre(),
            mapItemsReceta(s.getReceta())
        );
    }

    private ProductoResponseDTO mapToResponseParaListados(Producto s) {
        Integer stock = null;
    
        Sucursal sucursal = contexto.getSucursalLogueadaOrNull();
        
        if (sucursal != null) {
            stock = stockProductoService
                .obtenerPorProductoYSucursal(s.getId())
                .cantidadActual();
        }
    
        return mapToResponse(s, stock);
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