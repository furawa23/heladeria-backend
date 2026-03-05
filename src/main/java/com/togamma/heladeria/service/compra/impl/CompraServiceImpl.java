package com.togamma.heladeria.service.compra.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.compra.CompraRequestDTO;
import com.togamma.heladeria.dto.request.compra.DetCompraRequestDTO;
import com.togamma.heladeria.dto.response.compra.CompraResponseDTO;
import com.togamma.heladeria.dto.response.compra.DetCompraResponseDTO;
import com.togamma.heladeria.model.compra.Compra;
import com.togamma.heladeria.model.compra.DetalleCompra;
import com.togamma.heladeria.model.compra.EstadoCompra;
import com.togamma.heladeria.model.compra.Proveedor;
import com.togamma.heladeria.model.almacen.PresentacionProducto;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.repository.compra.CompraRepository;
import com.togamma.heladeria.repository.compra.ProveedorRepository;
import com.togamma.heladeria.service.almacen.AlmacenQueryService;
import com.togamma.heladeria.service.compra.CompraService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ContextService contexto;
    private final AlmacenQueryService almacenQuery;

    @Override
    public CompraResponseDTO crear(CompraRequestDTO dto) {
        if (compraRepository.existsByNumeroComprobanteAndSucursalId(
                dto.numeroComprobante(), contexto.getSucursalLogueada().getId())) {
                throw new RuntimeException("Ya existe una compra con este comprobante");
        }

        Compra compra = new Compra();
        compra.setSucursal(contexto.getSucursalLogueada());
        
        mapToEntity(compra, dto);
        
        gestionarDetalles(compra, dto.detalles());

        Compra guardada = compraRepository.save(compra);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponseDTO> listarTodas(Pageable pageable) {
        return compraRepository.findBySucursalId(contexto.getSucursalLogueada().getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CompraResponseDTO obtenerPorId(Long id) {
        Compra compra = compraRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        return mapToResponse(compra);
    }

    @Override
    public CompraResponseDTO actualizar(Long id, CompraRequestDTO dto) {
        Compra compra = compraRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
    
        if (compra.getEstado() != EstadoCompra.REGISTRADO) {
            throw new RuntimeException("Solo se pueden actualizar compras en estado REGISTRADO");
        }

        if (!compra.getNumeroComprobante().equals(dto.numeroComprobante()) && 
            compraRepository.existsByNumeroComprobanteAndSucursalId(dto.numeroComprobante(), contexto.getSucursalLogueada().getId())) {
            throw new RuntimeException("Ya existe otra compra con este comprobante");
        }
    
        revertirStock(compra);
    
        mapToEntity(compra, dto);
    
        gestionarDetalles(compra, dto.detalles());
    
        Compra actualizada = compraRepository.save(compra);
        return mapToResponse(actualizada);
    }
    
    @Override
    public void eliminar(Long id) {
        Compra compra = compraRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
    
        revertirStock(compra);
    
        compraRepository.delete(compra);
    }

    @Override
    public void confirmar(Long id) {
        Compra compra = compraRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        if (compra.getEstado() == EstadoCompra.CANCELADA) {
            throw new RuntimeException("No se puede confirmar una compra cancelada");
        }

        for (DetalleCompra detalle : compra.getDetalles()) {
            int factor = detalle.getPresentacion() != null ? detalle.getPresentacion().getFactor() : 1;
            int cantidadAfectar = detalle.getCantidad() * factor;

            almacenQuery.afectarStock(detalle.getProducto().getId(), contexto.getSucursalLogueada().getId(), cantidadAfectar);
        }

        compra.setEstado(EstadoCompra.CONFIRMADA);
        compraRepository.save(compra);
    }

    @Override
    public void cancelar(Long id) {
        Compra compra = compraRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        if (compra.getEstado() == EstadoCompra.CANCELADA) {
            return; 
        }

        revertirStock(compra);

        compra.setEstado(EstadoCompra.CANCELADA);
        compraRepository.save(compra);
    }

    private void gestionarDetalles(Compra compra, List<DetCompraRequestDTO> detallesDTO) {
        if (compra.getDetalles() == null) {
            compra.setDetalles(new ArrayList<>());
        } else {
            compra.getDetalles().clear();
        }

        if (detallesDTO == null || detallesDTO.isEmpty()) {
            compra.setTotal(0.0);
            return;
        }

        List<Long> productoIds = almacenQuery.extraerIdsProductos(detallesDTO);
        List<Long> presentacionIds = almacenQuery.extraerIdsPresentaciones(detallesDTO);

        Long empresaId = contexto.getEmpresaLogueada().getId();
        Map<Long, Producto> mapaProductos = almacenQuery.obtenerProductosEnMapa(productoIds, empresaId);
        Map<Long, PresentacionProducto> mapaPresentaciones = almacenQuery.obtenerPresentacionesEnMapa(presentacionIds, empresaId);
        double totalCalculado = 0.0;

        for (DetCompraRequestDTO itemDto : detallesDTO) {
            Producto producto = mapaProductos.get(itemDto.idProducto());
            if (producto == null) throw new RuntimeException("Producto ID " + itemDto.idProducto() + " no encontrado");

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(itemDto.cantidad());
            detalle.setPrecioUnitarioCompra(itemDto.precioUnitario());

            if (itemDto.idPresentacion() != null) {
                PresentacionProducto presentacion = mapaPresentaciones.get(itemDto.idPresentacion());
                if (presentacion == null) throw new RuntimeException("La presentación no existe");
                if (!presentacion.getProducto().getId().equals(producto.getId())) {
                    throw new RuntimeException("La presentación no corresponde al producto");
                }

                detalle.setPresentacion(presentacion);
            }

            double subtotal = itemDto.cantidad() * itemDto.precioUnitario();
            detalle.setSubtotal(subtotal);

            compra.getDetalles().add(detalle);
            totalCalculado += subtotal;
        }

        compra.setTotal(totalCalculado);
    }

    private void mapToEntity(Compra compra, CompraRequestDTO dto) {
        compra.setDescripcion(dto.descripcion());
        compra.setNumeroComprobante(dto.numeroComprobante());
        compra.setEstado(EstadoCompra.REGISTRADO);

        if (dto.idProveedor() != null) {
            Proveedor proveedor = proveedorRepository.findByIdAndEmpresaId(dto.idProveedor(), contexto.getEmpresaLogueada().getId())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
            compra.setProveedor(proveedor);
        }
    }

    private CompraResponseDTO mapToResponse(Compra c) {
        return new CompraResponseDTO(
            c.getId(),
            c.getCreatedAt(),
            c.getDescripcion(),
            c.getNumeroComprobante(),
            c.getTotal(),
            c.getEstado(),
            c.getProveedor() != null ? c.getProveedor().getRazonSocial() : null,
            mapDetallesResponse(c.getDetalles())
        );
    }

    private List<DetCompraResponseDTO> mapDetallesResponse(List<DetalleCompra> detalles) {
        if (detalles == null) return List.of();
        return detalles.stream()
            .map(d -> new DetCompraResponseDTO(
                d.getId(),
                d.getProducto().getId(),
                d.getProducto().getNombre(),
                d.getPresentacion() != null ? d.getPresentacion().getId() : null,
                d.getPresentacion() != null ? d.getPresentacion().getNombre() : null,
                d.getCantidad(),
                d.getPrecioUnitarioCompra(),
                d.getSubtotal()
            ))
            .toList();
    }

    private void revertirStock(Compra compra) {
        if (compra.getDetalles() == null || compra.getDetalles().isEmpty()) return;
    
        for (DetalleCompra detalle : compra.getDetalles()) {
            int factor = detalle.getPresentacion() != null ? detalle.getPresentacion().getFactor() : 1;
            int cantidadQuitar = detalle.getCantidad() * factor;
            
            almacenQuery.afectarStock(detalle.getProducto().getId(), contexto.getSucursalLogueada().getId(), -cantidadQuitar);
        }
    }

}