package com.togamma.heladeria.service.almacen.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.almacen.StockProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.StockProdResponseDTO;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.model.almacen.StockProducto;
import com.togamma.heladeria.model.seguridad.Sucursal;
import com.togamma.heladeria.repository.almacen.ProductoRepository;
import com.togamma.heladeria.repository.almacen.StockProductoRepository;
// Asumo que tienes este repositorio, si no, es necesario crearlo
import com.togamma.heladeria.repository.seguridad.SucursalRepository; 
import com.togamma.heladeria.service.almacen.StockProductoService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StockProductoServiceImpl implements StockProductoService {

    private final StockProductoRepository stockRepository;
    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;
    private final ContextService contexto;

    @Override
    public StockProdResponseDTO registrarIngreso(StockProdRequestDTO dto) {
        // 1. Validar que la Sucursal exista y pertenezca a la Empresa logueada
        Sucursal sucursal = sucursalRepository.findById(dto.idSucursal())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        if (!sucursal.getEmpresa().getId().equals(contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("No tiene permisos para operar en esta sucursal");
        }

        // 2. Validar Producto
        Producto producto = productoRepository.findByIdAndEmpresaId(dto.idProducto(), contexto.getEmpresaLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado o no autorizado"));

        // 3. Buscar si ya existe el registro de stock
        StockProducto stock = stockRepository.findByProductoIdAndSucursalId(dto.idProducto(), dto.idSucursal())
                .orElse(new StockProducto());

        // 4. Si es nuevo, asignamos relaciones
        if (stock.getId() == null) {
            stock.setProducto(producto);
            stock.setSucursal(sucursal);
            stock.setCantidad(0);
        }

        // 5. Validar cantidad a ingresar
        if (dto.cantidad() <= 0) {
            throw new RuntimeException("La cantidad a ingresar debe ser mayor a 0");
        }

        // 6. Actualizar stock (Sumar)
        stock.setCantidad(stock.getCantidad() + dto.cantidad());

        StockProducto guardado = stockRepository.save(stock);
        return mapToResponse(guardado);
    }

    @Override
    public StockProdResponseDTO ajustarCantidad(Long id, Integer nuevaCantidad) {
        
        StockProducto stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de stock no encontrado"));

        // Validar seguridad (Empresa)
        if (!stock.getSucursal().getEmpresa().getId().equals(contexto.getEmpresaLogueada().getId())) {
             throw new RuntimeException("No autorizado para modificar este stock");
        }

        if (nuevaCantidad < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        stock.setCantidad(nuevaCantidad);
        
        StockProducto actualizado = stockRepository.save(stock);
        return mapToResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockProdResponseDTO> listarPorProducto(Long idProducto, Pageable pageable) {

        boolean perteneceAEmpresa = productoRepository.existsByIdAndEmpresaId(
                idProducto, 
                contexto.getEmpresaLogueada().getId()
        );
    
        if (!perteneceAEmpresa) {
            throw new RuntimeException("Producto no encontrado o no autorizado"); 
        }
    
        return stockRepository.findByProductoId(idProducto, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StockProdResponseDTO obtenerPorId(Long id) {
        StockProducto stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado"));

        if (!stock.getSucursal().getEmpresa().getId().equals(contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("No autorizado");
        }

        return mapToResponse(stock);
    }

    private StockProdResponseDTO mapToResponse(StockProducto s) {
        return new StockProdResponseDTO(
            s.getId(),
            s.getUpdatedAt() != null ? s.getUpdatedAt() : s.getCreatedAt(),
            s.getProducto().getNombre(),
            s.getProducto().getUnidadBase(),
            s.getSucursal().getNombre(),
            s.getCantidad()
        );
    }
}