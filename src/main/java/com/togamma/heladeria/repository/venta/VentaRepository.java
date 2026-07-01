package com.togamma.heladeria.repository.venta;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import com.togamma.heladeria.model.venta.Venta;
import com.togamma.heladeria.model.venta.EstadoVenta;
import com.togamma.heladeria.repository.SucursalScopedRepository;

public interface VentaRepository extends SucursalScopedRepository<Venta> {
    
    Boolean existsByNumeroComprobanteAndSucursalId(String numeroComprobante, Long id);

    @Override
    @EntityGraph(attributePaths = {"mesa", "detalles.producto", "detalles.presentacion"})
    Optional<Venta> findByIdAndSucursalId(Long id, Long idSucursal);

    @Override
    @EntityGraph(attributePaths = {"mesa", "detalles.producto", "detalles.presentacion"})
    Page<Venta> findBySucursalId(Long idSucursal, Pageable pageable);

    @EntityGraph(attributePaths = {"mesa", "detalles.producto", "detalles.presentacion"})
    List<Venta> findBySucursalIdAndEstadoAndCreatedAtAfterAndDeletedAtIsNull(Long idSucursal, EstadoVenta estado, LocalDateTime startDate);
}