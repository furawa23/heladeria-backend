package com.togamma.heladeria.service.almacen;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.DetalleTransaccionDTO;
import com.togamma.heladeria.model.almacen.PresentacionProducto;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.model.almacen.StockProducto;
import com.togamma.heladeria.repository.almacen.PresentacionProductoRepository;
import com.togamma.heladeria.repository.almacen.ProductoRepository;
import com.togamma.heladeria.repository.almacen.StockProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlmacenQueryService {
    
    private final ProductoRepository productoRepository;
    private final StockProductoRepository stockRepository;
    private final PresentacionProductoRepository presentacionRepository;

    public Map<Long, Producto> obtenerProductosEnMapa(List<Long> productoIds, Long idEmpresa) {
        if (productoIds == null || productoIds.isEmpty()) return Map.of();
        return productoRepository.findByIdInAndEmpresaId(productoIds, idEmpresa).stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));
    }

    public Map<Long, PresentacionProducto> obtenerPresentacionesEnMapa(List<Long> presentacionIds, Long idEmpresa) {
         if (presentacionIds == null || presentacionIds.isEmpty()) return Map.of();
         return presentacionRepository.findByIdInAndProductoEmpresaId(presentacionIds, idEmpresa).stream()
                .collect(Collectors.toMap(PresentacionProducto::getId, p -> p));
    }

    public List<Long> extraerIdsProductos(List<? extends DetalleTransaccionDTO> detalles) {
        if (detalles == null) return List.of();
        return detalles.stream()
                .map(DetalleTransaccionDTO::idProducto)
                .distinct()
                .toList();
    }

    public List<Long> extraerIdsPresentaciones(List<? extends DetalleTransaccionDTO> detalles) {
        if (detalles == null) return List.of();
        return detalles.stream()
                .map(DetalleTransaccionDTO::idPresentacion)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    @Transactional
    public void afectarStock(Long productoId, Long sucursalId, int cantidadAfectar) {
        StockProducto stock = stockRepository.findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseThrow(() -> new RuntimeException("No existe stock para este producto en la sucursal"));

        int nuevoStock = stock.getCantidad() + cantidadAfectar;
        
        // Si la cantidad a afectar era negativa (Venta) y el stock no alcanza:
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente");
        }

        stock.setCantidad(nuevoStock);
        stockRepository.save(stock);
    }
}