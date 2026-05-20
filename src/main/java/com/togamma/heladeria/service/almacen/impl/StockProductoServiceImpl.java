package com.togamma.heladeria.service.almacen.impl;

import java.util.List;

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
    public void inicializarStock(Long idProducto, Long idSucursal) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
    
        StockProducto stock = new StockProducto();
        stock.setProducto(producto);
        stock.setSucursal(sucursal);
        stock.setCantidad(0);
        
        stockRepository.save(stock);
    }

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

        if (producto.getReceta() != null && !producto.getReceta().isEmpty()) {
            throw new RuntimeException("No se puede registrar stock físico de un producto preparado al momento (con receta).");
        }

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
        if (dto.cantidad() < 0) {
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

        if (stock.getProducto().getReceta() != null && !stock.getProducto().getReceta().isEmpty()) {
            throw new RuntimeException("No se puede ajustar stock físico de un producto preparado al momento (con receta).");
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
    public List<StockProdResponseDTO> listarPorProducto(Long idProducto) {

        boolean perteneceAEmpresa = productoRepository.existsByIdAndEmpresaId(
                idProducto, 
                contexto.getEmpresaLogueada().getId()
        );
    
        if (!perteneceAEmpresa) {
            throw new RuntimeException("Producto no encontrado o no autorizado"); 
        }
    
        return stockRepository.findByProductoId(idProducto)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockProdResponseDTO obtenerPorProductoYSucursal(Long idProducto) {

        Long idSucursal = contexto.getSucursalLogueada().getId();

        return stockRepository.findByProductoIdAndSucursalId(idProducto, idSucursal)
                .map(this::mapToResponse)
                .orElseGet(() -> {
                    // Si no hay registro físico, armamos una respuesta con stock 0
                    // Buscamos los nombres para que la vista (frontend) no reciba datos nulos
                    Producto p = productoRepository.findById(idProducto)
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                    Sucursal s = sucursalRepository.findById(idSucursal)
                            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

                    return new StockProdResponseDTO(
                            null, // id nulo porque aún no existe en BD
                            null, // fecha nula
                            p.getNombre(),
                            p.getUnidadBase(),
                            s.getNombre(),
                            0 // ESTA ES LA MAGIA: Devolvemos 0
                    );
                });
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