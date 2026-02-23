package com.togamma.heladeria.service.compra.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.togamma.heladeria.model.compra.Proveedor;
import com.togamma.heladeria.model.almacen.PresentacionProducto;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.repository.compra.CompraRepository;
import com.togamma.heladeria.repository.compra.ProveedorRepository;
import com.togamma.heladeria.repository.almacen.PresentacionProductoRepository;
import com.togamma.heladeria.repository.almacen.ProductoRepository;
import com.togamma.heladeria.service.compra.CompraService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final PresentacionProductoRepository presentacionRepository;
    private final ContextService contexto;

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
        if (compraRepository.existsByNumeroComprobanteAndSucursalId(
            dto.numeroComprobante(), contexto.getSucursalLogueada().getId())) {
            throw new RuntimeException("Ya existe una compra con este comprobante");
        }
        
        Compra compra = compraRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        mapToEntity(compra, dto);

        gestionarDetalles(compra, dto.detalles());

        Compra actualizada = compraRepository.save(compra);
        return mapToResponse(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        // Soft delete manejado por la entidad y repositorio
        if (!compraRepository.existsByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())) {
            throw new RuntimeException("Compra no encontrada");
        }
        compraRepository.deleteById(id);
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

        // 1. Obtener IDs de Productos y Presentaciones
        List<Long> productoIds = detallesDTO.stream()
                .map(DetCompraRequestDTO::idProducto)
                .toList();

        List<Long> presentacionIds = detallesDTO.stream()
                .map(DetCompraRequestDTO::idPresentacion)
                .filter(id -> id != null)
                .distinct() // Evitar duplicados
                .toList();

        // 2. Buscar en Base de Datos
        List<Producto> productosEncontrados = productoRepository.findByIdInAndEmpresaId(
                productoIds, contexto.getEmpresaLogueada().getId());
        Map<Long, Producto> mapaProductos = productosEncontrados.stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

        Map<Long, PresentacionProducto> mapaPresentaciones;
        if (!presentacionIds.isEmpty()) {
            List<PresentacionProducto> presentacionesEncontradas = presentacionRepository.findByIdInAndProductoEmpresaId(
                    presentacionIds, contexto.getEmpresaLogueada().getId());
            mapaPresentaciones = presentacionesEncontradas.stream()
                    .collect(Collectors.toMap(PresentacionProducto::getId, p -> p));
        } else {
            mapaPresentaciones = Map.of();
        }

        double totalCalculado = 0.0;

        for (DetCompraRequestDTO itemDto : detallesDTO) {
            Producto producto = mapaProductos.get(itemDto.idProducto());

            if (producto == null) {
                throw new RuntimeException("Producto ID " + itemDto.idProducto() + " no encontrado");
            }
            
            if (itemDto.cantidad() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a 0");
            }

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(itemDto.cantidad());
            detalle.setPrecioUnitarioCompra(itemDto.precioUnitario());

            if (itemDto.idPresentacion() != null) {
                PresentacionProducto presentacion = mapaPresentaciones.get(itemDto.idPresentacion());

                if (presentacion == null) {
                    throw new RuntimeException("La presentación solicitada no existe o no pertenece a su empresa");
                }

                if (!presentacion.getProducto().getId().equals(producto.getId())) {
                    throw new RuntimeException("La presentación '" + presentacion.getNombre() + 
                        "' no corresponde al producto '" + producto.getNombre() + "'");
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
        compra.setEstado("REGISTRADO");

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
                d.getPresentacion().getId(),
                d.getPresentacion().getNombre(),
                d.getCantidad(),
                d.getPrecioUnitarioCompra(),
                d.getSubtotal()
            ))
            .toList();
    }
}