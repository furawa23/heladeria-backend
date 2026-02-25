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
        if (dto.idMesa() == null) {
            throw new RuntimeException("No hay mesa asignada");
        }

        Mesa mesa = mesaRepository.findByIdAndSucursalId(dto.idMesa(), contexto.getSucursalLogueada().getId())
                    .orElseThrow(() -> new RuntimeException("No existe la mesa"));
        
        if (!mesa.getLibre()) {
            throw new RuntimeException("mesa ocupada");
        }

        mesa.setLibre(false);
        
        return crearRapida(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponseDTO> listarTodas(Pageable pageable) {
        return ventaRepository.findBySucursalId(contexto.getSucursalLogueada().getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO obtenerPorId(Long id) {
        Venta venta = ventaRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        return mapToResponse(venta);
    }

    @Override
    public VentaResponseDTO actualizar(Long id, VentaRequestDTO dto) {
        Venta venta = ventaRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (venta.getEstado() != EstadoVenta.CREADA) {
            throw new RuntimeException("Solo se pueden actualizar ventas en estado CREADA");
        }
        
        if (!venta.getNumeroComprobante().equals(dto.numeroComprobante()) && 
            ventaRepository.existsByNumeroComprobanteAndSucursalId(dto.numeroComprobante(), contexto.getSucursalLogueada().getId())) {
            throw new RuntimeException("Ya existe otra venta con este comprobante");
        }

        // Opcional: Lógica para manejar el cambio de Mesa
        Mesa mesaAntigua = venta.getMesa();

        revertirStock(venta);

        mapToEntity(venta, dto);

        // Si la mesa cambió, libera la antigua y ocupa la nueva
        if (mesaAntigua != null && (venta.getMesa() == null || !mesaAntigua.getId().equals(venta.getMesa().getId()))) {
            mesaAntigua.setLibre(true);
            if (venta.getMesa() != null) {
                venta.getMesa().setLibre(false);
            }
        }

        gestionarDetalles(venta, dto.detalles());

        Venta actualizada = ventaRepository.save(venta);
        return mapToResponse(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        Venta venta = ventaRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        revertirStock(venta);

        if (venta.getMesa() != null) {
            venta.getMesa().setLibre(true);
        }

        ventaRepository.delete(venta);
    }

    @Override
    public void cobrar(Long id) {
        Venta venta = ventaRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (venta.getEstado() == EstadoVenta.CANCELADA) {
            throw new RuntimeException("No se puede cobrar una venta que está cancelada");
        }

        venta.setEstado(EstadoVenta.COBRADA);
        
        if (venta.getMesa() != null) {
            venta.getMesa().setLibre(true);
        }

        ventaRepository.save(venta);
    }

    @Override
    public void cancelar(Long id) {
        Venta venta = ventaRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (venta.getEstado() == EstadoVenta.CANCELADA) {
            return;
        }

        revertirStock(venta);

        if (venta.getMesa() != null) {
            venta.getMesa().setLibre(true);
        }

        venta.setEstado(EstadoVenta.CANCELADA);
        ventaRepository.save(venta);
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

        List<Long> productoIds = almacenQuery.extraerIdsProductos(detallesDTO);
        List<Long> presentacionIds = almacenQuery.extraerIdsPresentaciones(detallesDTO);

        Long empresaId = contexto.getEmpresaLogueada().getId();
        Map<Long, Producto> mapaProductos = almacenQuery.obtenerProductosEnMapa(productoIds, empresaId);
        Map<Long, PresentacionProducto> mapaPresentaciones = almacenQuery.obtenerPresentacionesEnMapa(presentacionIds, empresaId);

        double totalCalculado = 0.0;

        for (DetVentaRequestDTO itemDto : detallesDTO) {
            Producto producto = mapaProductos.get(itemDto.idProducto());

            if (producto == null) throw new RuntimeException("Producto ID " + itemDto.idProducto() + " no encontrado");
            if (itemDto.cantidad() <= 0) throw new RuntimeException("La cantidad debe ser mayor a 0");

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(itemDto.cantidad());

            // Variables por defecto (si se vende por unidad base)
            int factorConversion = 1;
            double precioCobrar = producto.getPrecioUnitarioVenta();

            // Si hay presentación, actualizamos el factor y el precio
            if (itemDto.idPresentacion() != null) {
                PresentacionProducto presentacion = mapaPresentaciones.get(itemDto.idPresentacion());
                if (presentacion == null) throw new RuntimeException("La presentación no existe");
                if (!presentacion.getProducto().getId().equals(producto.getId())) {
                    throw new RuntimeException("La presentación no corresponde al producto");
                }
                
                detalle.setPresentacion(presentacion);
                factorConversion = presentacion.getFactor();
                precioCobrar = presentacion.getPrecioVenta();
            }

            // Afectamos el stock multiplicando por el factor (ej: 2 cajas * 12 = 24 unidades a restar)
            int cantidadRealAfectar = itemDto.cantidad() * factorConversion;
            almacenQuery.afectarStock(producto.getId(), contexto.getSucursalLogueada().getId(), -cantidadRealAfectar);

            // Calculamos subtotales y seteamos valores
            detalle.setPrecioUnitario(precioCobrar);
            double subtotal = itemDto.cantidad() * precioCobrar;
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
                d.getPresentacion() != null ? d.getPresentacion().getId() : null,
                d.getPresentacion() != null ? d.getPresentacion().getNombre() : null,
                d.getCantidad(),
                d.getPrecioUnitario(),
                d.getSubtotal()
            ))
            .toList();
    }

    private void revertirStock(Venta venta) {
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) return;

        for (DetalleVenta detalle : venta.getDetalles()) {
            int factor = detalle.getPresentacion() != null ? detalle.getPresentacion().getFactor() : 1;
            int cantidadDevolver = detalle.getCantidad() * factor;
            
            almacenQuery.afectarStock(detalle.getProducto().getId(), contexto.getSucursalLogueada().getId(), cantidadDevolver);
        }
    }

}
