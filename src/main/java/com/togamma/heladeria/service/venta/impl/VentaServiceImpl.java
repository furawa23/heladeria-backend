package com.togamma.heladeria.service.venta.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.venta.DetVentaRequestDTO;
import com.togamma.heladeria.dto.request.venta.VentaRequestDTO;
import com.togamma.heladeria.dto.response.venta.DetVentaResponseDTO;
import com.togamma.heladeria.dto.response.venta.VentaResponseDTO;
import com.togamma.heladeria.model.almacen.PresentacionProducto;
import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.model.venta.DetalleVenta;
import com.togamma.heladeria.model.venta.EstadoVenta;
import com.togamma.heladeria.model.venta.Mesa;
import com.togamma.heladeria.model.venta.Venta;
import com.togamma.heladeria.repository.venta.MesaRepository;
import com.togamma.heladeria.repository.venta.VentaRepository;
import com.togamma.heladeria.service.almacen.AlmacenQueryService;
import com.togamma.heladeria.service.seguridad.ContextService;
import com.togamma.heladeria.service.venta.VentaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final MesaRepository mesaRepository;
    private final ContextService contexto;
    private final AlmacenQueryService almacenQuery;

    @Override
    public VentaResponseDTO crearRapida(VentaRequestDTO dto) {
        if (ventaRepository.existsByNumeroComprobanteAndSucursalId(
            dto.numeroComprobante(), contexto.getSucursalLogueada().getId())) {
            throw new RuntimeException("Ya existe una venta con este comprobante");
        }

        Venta venta = new Venta();
        venta.setSucursal(contexto.getSucursalLogueada());

        mapToEntity(venta, dto);

        gestionarDetalles(venta, dto.detalles());

        Venta guardada = ventaRepository.save(venta);
        return mapToResponse(guardada);
    }

    @Override
    public VentaResponseDTO crearEnMesa(VentaRequestDTO dto) {
        if (dto.idMesa() != null) {
            crearRapida(dto);
        }

        throw new RuntimeException("No hay mesa asignada");
    }

    @Override
    public Page<VentaResponseDTO> listarTodas(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listarTodas'");
    }

    @Override
    public VentaResponseDTO obtenerPorId(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerPorId'");
    }

    @Override
    public VentaResponseDTO actualizar(Long id, VentaRequestDTO dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
    }

    @Override
    public void eliminar(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
    }

    private void gestionarDetalles(Venta venta, List<DetVentaRequestDTO> detallesDTO) {
        if (venta.getDetalles() == null) {
            venta.setDetalles(new ArrayList<>());
        } else {
            venta.getDetalles().clear();
        }

        if (detallesDTO == null || detallesDTO.isEmpty()) {
            venta.setTotal(0.0);
            return;
        }

        // 1. Obtener IDs de Productos y Presentaciones
        List<Long> productoIds = detallesDTO.stream()
                .map(DetVentaRequestDTO::idProducto)
                .toList();

        List<Long> presentacionIds = detallesDTO.stream()
                .map(DetVentaRequestDTO::idPresentacion)
                .filter(id -> id != null)
                .distinct() // Evitar duplicados
                .toList();

        // 2. Buscar en Base de Datos
        Long empresaId = contexto.getEmpresaLogueada().getId();
        Map<Long, Producto> mapaProductos = almacenQuery.obtenerProductosEnMapa(productoIds, empresaId);
        Map<Long, PresentacionProducto> mapaPresentaciones = almacenQuery.obtenerPresentacionesEnMapa(presentacionIds, empresaId);

        double totalCalculado = 0.0;

        for (DetVentaRequestDTO itemDto : detallesDTO) {
            Producto producto = mapaProductos.get(itemDto.idProducto());

            if (producto == null) {
                throw new RuntimeException("Producto ID " + itemDto.idProducto() + " no encontrado");
            }
            
            if (itemDto.cantidad() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a 0");
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(itemDto.cantidad());
            detalle.setPrecioUnitario(itemDto.precioUnitario());

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

            venta.getDetalles().add(detalle);
            totalCalculado += subtotal;
        }

        venta.setTotal(totalCalculado);
    }

    private void mapToEntity(Venta venta, VentaRequestDTO dto) {
        venta.setNumeroComprobante(dto.numeroComprobante());
        venta.setEstado(EstadoVenta.CREADA);

        if (dto.idMesa() != null) {
            Mesa mesa = mesaRepository.findByIdAndSucursalId(dto.idMesa(), contexto.getSucursalLogueada().getId())
                    .orElseThrow(() -> new RuntimeException("mesa no encontrada"));
            venta.setMesa(mesa);
        }
    }

    private VentaResponseDTO mapToResponse(Venta v) {
        return new VentaResponseDTO(
            v.getId(),
            v.getCreatedAt(),
            v.getNumeroComprobante(),
            v.getTotal(),
            v.getEstado(),
            v.getMesa() != null ? v.getMesa().getNumero() : null,
            mapDetallesResponse(v.getDetalles())
        );
    }

    private List<DetVentaResponseDTO> mapDetallesResponse(List<DetalleVenta> detalles) {
        if (detalles == null) return List.of();
        return detalles.stream()
            .map(d -> new DetVentaResponseDTO(
                d.getId(),
                d.getProducto().getId(),
                d.getProducto().getNombre(),
                d.getPresentacion().getId(),
                d.getPresentacion().getNombre(),
                d.getCantidad(),
                d.getPrecioUnitario(),
                d.getSubtotal()
            ))
            .toList();
    }

}
