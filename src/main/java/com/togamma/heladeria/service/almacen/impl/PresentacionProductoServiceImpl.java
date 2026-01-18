package com.togamma.heladeria.service.almacen.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.almacen.PresentacionProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.PresentacionProdResponseDTO;
import com.togamma.heladeria.model.almacen.PresentacionProducto;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.repository.almacen.PresentacionProductoRepository;
import com.togamma.heladeria.repository.almacen.ProductoRepository;
import com.togamma.heladeria.service.almacen.PresentacionProductoService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PresentacionProductoServiceImpl implements PresentacionProductoService {

    private final PresentacionProductoRepository presentacionRepository;
    private final ProductoRepository productoRepository;
    private final ContextService contexto;

    @Override
    public PresentacionProdResponseDTO crear(PresentacionProdRequestDTO dto) {
        
        if(presentacionRepository.existsByNombreAndProductoId(dto.nombre(), dto.idProducto())) {
            throw new RuntimeException("Ya existe una presentación igual para el producto actual");
        }

        Producto productoActual = productoRepository.findByIdAndEmpresaId(dto.idProducto(), contexto.getEmpresaLogueada().getId())
                                                    .orElseThrow(() -> new RuntimeException("Producto no existe"));

        PresentacionProducto presentacion = new PresentacionProducto();

        if(dto.factor() <= 0) {
            throw new RuntimeException("Factor no puede ser menor o igual a 0");
        }

        presentacion.setNombre(dto.nombre());
        presentacion.setFactor(dto.factor());
        presentacion.setProducto(productoActual);

        PresentacionProducto guardada = presentacionRepository.save(presentacion);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresentacionProdResponseDTO> listarPorProducto(Long idProducto, Pageable pageable) {
        
        boolean perteneceAEmpresa = productoRepository.existsByIdAndEmpresaId(idProducto, contexto.getEmpresaLogueada().getId());
    
        if (!perteneceAEmpresa) {
            throw new RuntimeException("Producto no encontrado o no autorizado"); 
        }
    
        return presentacionRepository.findByProductoId(idProducto, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PresentacionProdResponseDTO obtenerPorId(Long id) {
        PresentacionProducto presentacion = presentacionRepository.findByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Presentación de Producto no encontrada"));

        return mapToResponse(presentacion);
    }

    @Override
    public PresentacionProdResponseDTO actualizar(Long id, PresentacionProdRequestDTO dto) {
    
        PresentacionProducto presentacion = presentacionRepository.findByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())
                                            .orElseThrow(() -> new RuntimeException("Presentación de Producto no encontrada"));
    
        if (!presentacion.getNombre().equalsIgnoreCase(dto.nombre())) {
            if(presentacionRepository.existsByNombreAndProductoId(dto.nombre(), dto.idProducto())) {
                throw new RuntimeException("Ya existe otra presentación con este nombre para el producto");
            }
        }
    
        Producto productoActual = productoRepository.findByIdAndEmpresaId(
                dto.idProducto(), 
                contexto.getEmpresaLogueada().getId()
        ).orElseThrow(() -> new RuntimeException("Producto no existe"));
    
        if(dto.factor() <= 0) {
            throw new RuntimeException("Factor no puede ser menor o igual a 0");
        }
        
        presentacion.setNombre(dto.nombre());
        presentacion.setFactor(dto.factor());
        presentacion.setProducto(productoActual);
    
        PresentacionProducto actualizada = presentacionRepository.save(presentacion);
        return mapToResponse(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        if (!presentacionRepository.existsByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("Presentación de Producto no encontrada");
        }
        presentacionRepository.deleteById(id);
    }

    private PresentacionProdResponseDTO mapToResponse(PresentacionProducto s) {
        return new PresentacionProdResponseDTO(
            s.getId(),
            s.getCreatedAt(),
            s.getNombre(),
            s.getFactor(),
            s.getProducto().getNombre()
        );
    }
}