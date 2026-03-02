package com.togamma.heladeria.service.caja.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.caja.CajaRequestDTO;
import com.togamma.heladeria.dto.response.caja.CajaResponseDTO;
import com.togamma.heladeria.model.caja.Caja;
import com.togamma.heladeria.model.caja.EstadoCaja;
import com.togamma.heladeria.model.caja.MovimientoCaja;
import com.togamma.heladeria.model.caja.TipoMovimiento;
import com.togamma.heladeria.repository.caja.CajaRepository;
import com.togamma.heladeria.repository.caja.MovimientoCajaRepository;
import com.togamma.heladeria.service.caja.CajaService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CajaServiceImpl implements CajaService {

    private final CajaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final ContextService contexto;

    @Override
    public CajaResponseDTO abrirCaja(CajaRequestDTO dto) {
        Long sucursalId = contexto.getSucursalLogueada().getId();

        // Validar que no haya otra caja abierta
        if (cajaRepository.existsBySucursalIdAndEstado(sucursalId, EstadoCaja.ABIERTO)) {
            throw new RuntimeException("Ya existe una caja abierta en esta sucursal");
        }

        if (dto.montoInicial() == null || dto.montoInicial() < 0) {
            throw new RuntimeException("El monto inicial debe ser mayor o igual a cero");
        }

        Caja caja = new Caja();
        caja.setSucursal(contexto.getSucursalLogueada());
        caja.setFechaApertura(LocalDateTime.now());
        caja.setMontoInicial(dto.montoInicial());
        caja.setEstado(EstadoCaja.ABIERTO);

        Caja guardada = cajaRepository.save(caja);
        return mapToResponse(guardada);
    }

    @Override
    public CajaResponseDTO cerrarCaja(Long id) {
        Long sucursalId = contexto.getSucursalLogueada().getId();
        Caja caja = cajaRepository.findByIdAndSucursalId(id, sucursalId)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));

        if (caja.getEstado() == EstadoCaja.CERRADO) {
            throw new RuntimeException("La caja ya se encuentra cerrada");
        }

        // Calcular el monto final basado en los movimientos
        List<MovimientoCaja> movimientos = movimientoCajaRepository.findByCajaId(caja.getId());
        double totalIngresos = 0.0;
        double totalEgresos = 0.0;

        for (MovimientoCaja mov : movimientos) {
            if (mov.getTipo() == TipoMovimiento.INGRESO) {
                totalIngresos += mov.getMonto();
            } else if (mov.getTipo() == TipoMovimiento.EGRESO) {
                totalEgresos += mov.getMonto();
            }
        }

        Double montoFinalCalculado = caja.getMontoInicial() + totalIngresos - totalEgresos;

        caja.setMontoFinal(montoFinalCalculado);
        caja.setFechaCierre(LocalDateTime.now());
        caja.setEstado(EstadoCaja.CERRADO);

        Caja guardada = cajaRepository.save(caja);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponseDTO obtenerCajaAbierta() {
        Long sucursalId = contexto.getSucursalLogueada().getId();
        Caja caja = cajaRepository.findBySucursalIdAndEstado(sucursalId, EstadoCaja.ABIERTO)
                .orElseThrow(() -> new RuntimeException("No hay ninguna caja abierta actualmente"));
        
        return mapToResponse(caja);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CajaResponseDTO> listarTodas(Pageable pageable) {
        Long sucursalId = contexto.getSucursalLogueada().getId();
        return cajaRepository.findBySucursalId(sucursalId, pageable)
                .map(this::mapToResponse);
    }

    private CajaResponseDTO mapToResponse(Caja caja) {
        return new CajaResponseDTO(
            caja.getId(),
            caja.getFechaApertura(),
            caja.getFechaCierre(),
            caja.getMontoInicial(),
            caja.getMontoFinal(),
            caja.getEstado()
        );
    }
}