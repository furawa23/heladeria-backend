package com.togamma.heladeria.service.venta.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.venta.MesaRequestDTO;
import com.togamma.heladeria.dto.response.venta.MesaResponseDTO;
import com.togamma.heladeria.model.venta.Mesa;
import com.togamma.heladeria.repository.venta.MesaRepository;
import com.togamma.heladeria.service.seguridad.ContextService;
import com.togamma.heladeria.service.venta.MesaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;
    private final ContextService contexto;

    @Override
    public MesaResponseDTO crear(MesaRequestDTO dto) {

        if (mesaRepository.existsByNumeroAndSucursalId(dto.numero(), contexto.getSucursalLogueada().getId())) {
            throw new RuntimeException("La categoría ya existe");
        }

        Mesa mesa = new Mesa();
        mesa.setNumero(dto.numero());
        mesa.setSucursal(contexto.getSucursalLogueada());
        mesa.setLibre(true);

        Mesa guardada = mesaRepository.save(mesa);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MesaResponseDTO> listarTodas(Pageable page) {
        return mesaRepository.findBySucursalId(contexto.getSucursalLogueada().getId(), page)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MesaResponseDTO obtenerPorId(Long id) {
        Mesa mesa = mesaRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("mesa de Producto no encontrada"));

        return mapToResponse(mesa);
    }

    @Override
    public MesaResponseDTO actualizar(Long id, MesaRequestDTO dto) {
    
        Mesa mesa = mesaRepository.findByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())
                .orElseThrow(() -> new RuntimeException("mesa de Producto no encontrada"));

        if (!mesa.getNumero().equals(dto.numero())) {
            if (mesaRepository.existsByNumeroAndSucursalId(dto.numero(), contexto.getSucursalLogueada().getId())) {
                throw new RuntimeException("Ya existe una categoría con ese Numero");
            }
        }
        
        mesa.setNumero(dto.numero());
    
        Mesa actualizada = mesaRepository.save(mesa);
        return mapToResponse(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        if (!mesaRepository.existsByIdAndSucursalId(id, contexto.getSucursalLogueada().getId())) {
            throw new RuntimeException("mesa de Producto no encontrada");
        }
        mesaRepository.deleteById(id);
    }

    private MesaResponseDTO mapToResponse(Mesa s) {
        return new MesaResponseDTO(
            s.getId(),
            s.getNumero(),
            s.getLibre(),
            s.getSucursal().getNombre()
        );
    }
}