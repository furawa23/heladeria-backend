package com.togamma.heladeria.service.sabor.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.sabor.SaborRequestDTO;
import com.togamma.heladeria.dto.response.sabor.SaborResponseDTO;
import com.togamma.heladeria.model.sabores.Sabor;
import com.togamma.heladeria.repository.sabor.SaborRepository;
import com.togamma.heladeria.service.sabor.SaborService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SaborServiceImpl implements SaborService {

    private final SaborRepository saborRepository;
    private final ContextService contexto;
    @Override
    public SaborResponseDTO crear(SaborRequestDTO dto) {
        
        if (saborRepository.existsByNombreAndEmpresaId(dto.nombre(), contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("El sabor ya existe");
        }

        Sabor sabor = new Sabor();
        sabor.setNombre(dto.nombre());
        sabor.setPrecioAdicional(dto.precioAdicional());
        sabor.setEmpresa(contexto.getEmpresaLogueada());

        Sabor guardada = saborRepository.save(sabor);
        return SaborResponseDTO.mapToResponse(guardada);
    }

    @Override
    public Page<SaborResponseDTO> listarTodas(Pageable pageable) {
        return saborRepository.findByEmpresaId(contexto.getEmpresaLogueada().getId(), pageable)
                .map(SaborResponseDTO::mapToResponse);
    }
    @Override
    public SaborResponseDTO obtenerPorId(Long id) {
        Sabor sabor = saborRepository.findByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())
                        .orElseThrow(() -> new RuntimeException("sabor no encontrado"));

        return SaborResponseDTO.mapToResponse(sabor);
    }

    @Override
    public SaborResponseDTO actualizar(Long id, SaborRequestDTO dto) {
        Sabor sabor = saborRepository.findByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())
                        .orElseThrow(() -> new RuntimeException("sabor no encontrado"));

        if (!sabor.getNombre().equalsIgnoreCase(dto.nombre())) {
            if (saborRepository.existsByNombreAndEmpresaIdAndIdNot(dto.nombre(), contexto.getEmpresaLogueada().getId(), id)) {
                throw new RuntimeException("Ya existe un sabor con ese nombre");
            }
        }

        sabor.setNombre(dto.nombre());
        sabor.setPrecioAdicional(dto.precioAdicional());

        Sabor actualizado = saborRepository.save(sabor);
        return SaborResponseDTO.mapToResponse(actualizado);    
    }

    @Override
    public void eliminar(Long id) {
        if (!saborRepository.existsByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("sabor no encontrado");
        }
        saborRepository.deleteById(id);
    }

}
