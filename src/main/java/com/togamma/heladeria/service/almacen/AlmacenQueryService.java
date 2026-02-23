package com.togamma.heladeria.service.almacen;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.togamma.heladeria.model.almacen.PresentacionProducto;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.repository.almacen.PresentacionProductoRepository;
import com.togamma.heladeria.repository.almacen.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlmacenQueryService {
    
    private final ProductoRepository productoRepository;
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
}