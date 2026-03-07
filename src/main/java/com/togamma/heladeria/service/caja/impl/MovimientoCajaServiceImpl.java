package com.togamma.heladeria.service.caja.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.caja.MovimientoCajaRequestDTO;
import com.togamma.heladeria.dto.response.caja.MovimientoCajaResponseDTO;
import com.togamma.heladeria.model.caja.Caja;
import com.togamma.heladeria.model.caja.EstadoCaja;
import com.togamma.heladeria.model.caja.MovimientoCaja;
import com.togamma.heladeria.repository.caja.CajaRepository;
import com.togamma.heladeria.repository.caja.MovimientoCajaRepository;
import com.togamma.heladeria.repository.compra.CompraRepository;
import com.togamma.heladeria.repository.venta.VentaRepository;
import com.togamma.heladeria.service.caja.MovimientoCajaService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoCajaServiceImpl implements MovimientoCajaService {

    private final MovimientoCajaRepository movimientoCajaRepository;
    private final CajaRepository cajaRepository;
    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;
    private final ContextService contexto;

    @Override
    public MovimientoCajaResponseDTO registrarMovimiento(MovimientoCajaRequestDTO dto) {
        Long sucursalId = contexto.getSucursalLogueada().getId();

        // Buscar la caja abierta actual
        Caja cajaActiva = cajaRepository.findBySucursalIdAndEstado(sucursalId, EstadoCaja.ABIERTO)
                .orElseThrow(() -> new RuntimeException("No se puede registrar el movimiento: No hay una caja abierta"));

        if (dto.monto() == null || dto.monto() <= 0) {
            throw new RuntimeException("El monto del movimiento debe ser mayor a cero");
        }

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(cajaActiva);
        movimiento.setTipo(dto.tipo());
        movimiento.setMonto(dto.monto());
        movimiento.setMetodoPago(dto.metodoPago());

        // Enlazar referencias si existen
        if (dto.idVenta() != 0) {
            movimiento.setReferenciaVenta(
                ventaRepository.findByIdAndSucursalId(dto.idVenta(), sucursalId)
                .orElseThrow(() -> new RuntimeException("Venta de referencia no encontrada"))
            );
        }

        if (dto.idCompra() != 0) {
            movimiento.setReferenciaCompra(
                compraRepository.findByIdAndSucursalId(dto.idCompra(), sucursalId)
                .orElseThrow(() -> new RuntimeException("Compra de referencia no encontrada"))
            );
        }

        MovimientoCaja guardado = movimientoCajaRepository.save(movimiento);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoCajaResponseDTO> listarPorCaja(Long idCaja, Pageable pageable) {
        Long sucursalId = contexto.getSucursalLogueada().getId();
        
        // Validar que la caja pertenece a la sucursal actual
        cajaRepository.findByIdAndSucursalId(idCaja, sucursalId)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada en esta sucursal"));

        return movimientoCajaRepository.findByCajaId(idCaja, pageable)
                .map(this::mapToResponse);
    }

    private MovimientoCajaResponseDTO mapToResponse(MovimientoCaja mov) {
        Long idVenta = mov.getReferenciaVenta() != null ? mov.getReferenciaVenta().getId() : null;
        Long idCompra = mov.getReferenciaCompra() != null ? mov.getReferenciaCompra().getId() : null;

        return new MovimientoCajaResponseDTO(
            mov.getId(),
            mov.getTipo(),
            mov.getMonto(),
            idVenta,
            idCompra,
            mov.getMetodoPago(),
            mov.getCreatedAt()
        );
    }
}