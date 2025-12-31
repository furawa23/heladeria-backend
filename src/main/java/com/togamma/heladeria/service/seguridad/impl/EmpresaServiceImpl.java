package com.togamma.heladeria.service.seguridad.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.seguridad.EmpresaRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.EmpresaResponseDTO;
import com.togamma.heladeria.dto.response.seguridad.SucursalResponseDTO;
import com.togamma.heladeria.model.seguridad.Empresa;
import com.togamma.heladeria.model.seguridad.Sucursal;
import com.togamma.heladeria.repository.seguridad.EmpresaRepository;
import com.togamma.heladeria.service.seguridad.EmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Override
    @Transactional
    public EmpresaResponseDTO crear(EmpresaRequestDTO dto) {

        Empresa empresa = new Empresa();
        empresa.setRuc(dto.ruc());
        empresa.setRazonSocial(dto.razonSocial());
        empresa.setNombreDuenio(dto.nombreDuenio());
        empresa.setTelefono(dto.telefono());

        Empresa guardada = empresaRepository.save(empresa);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaResponseDTO> listarTodas(Pageable page) {

        return empresaRepository.findAll(page)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponseDTO obtenerPorId(Long id) {

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        return mapToResponse(empresa);
    }

    @Override
    @Transactional
    public EmpresaResponseDTO actualizar(Long id, EmpresaRequestDTO dto) {

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        empresa.setRuc(dto.ruc());
        empresa.setRazonSocial(dto.razonSocial());
        empresa.setNombreDuenio(dto.nombreDuenio());
        empresa.setTelefono(dto.telefono());

        return mapToResponse(empresa);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        if (!empresaRepository.existsById(id)) {
            throw new RuntimeException("Empresa no encontrada");
        }

        empresaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void restaurar(Long id) {
        Empresa empresa = empresaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
    
        empresa.setDeletedAt(null);
        empresaRepository.save(empresa);
    }

    private EmpresaResponseDTO mapToResponse(Empresa e) {
        return new EmpresaResponseDTO(
            e.getId(),
            e.getCreatedAt(),
            e.getUpdatedAt(),
            e.getDeletedAt(),
            e.getRuc(),
            e.getRazonSocial(),
            e.getNombreDuenio(),
            e.getTelefono(),
            mapSucursales(e.getSucursales())
        );
    }
    
    private List<SucursalResponseDTO> mapSucursales(List<Sucursal> sucursales) {
        if (sucursales == null) return List.of();
        return sucursales.stream()
            .map(s -> new SucursalResponseDTO(
                s.getId(),
                s.getCreatedAt(),
                s.getUpdatedAt(),
                s.getDeletedAt(),
                s.getNombre(),
                s.getDireccion()))
            .toList();
    }
}
