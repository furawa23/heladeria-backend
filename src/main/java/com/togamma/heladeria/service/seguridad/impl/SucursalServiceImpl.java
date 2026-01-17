package com.togamma.heladeria.service.seguridad.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.seguridad.SucursalRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.SucursalResponseDTO;
import com.togamma.heladeria.model.seguridad.Empresa;
import com.togamma.heladeria.model.seguridad.Sucursal;
import com.togamma.heladeria.repository.seguridad.EmpresaRepository;
import com.togamma.heladeria.repository.seguridad.SucursalRepository;
import com.togamma.heladeria.service.seguridad.SucursalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final EmpresaRepository empresaRepository;

    @Override
    public SucursalResponseDTO crear(SucursalRequestDTO dto) {
        Empresa empresa = empresaRepository.findById(dto.idEmpresa())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + dto.idEmpresa()));

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(dto.nombre());
        sucursal.setDireccion(dto.direccion());
        sucursal.setEmpresa(empresa);

        Sucursal guardada = sucursalRepository.save(sucursal);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SucursalResponseDTO> listarTodas(Pageable page) {
        return sucursalRepository.findAll(page)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SucursalResponseDTO> listarPorEmpresa(Long idEmpresa, Pageable page) {
        return sucursalRepository.findByEmpresaId(idEmpresa, page)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponseDTO obtenerPorId(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        return mapToResponse(sucursal);
    }

    @Override
    public SucursalResponseDTO actualizar(Long id, SucursalRequestDTO dto) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        // Si el DTO trae un ID de empresa diferente, actualizamos la relación
        if (dto.idEmpresa() != null && !dto.idEmpresa().equals(sucursal.getEmpresa().getId())) {
            Empresa nuevaEmpresa = empresaRepository.findById(dto.idEmpresa())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + dto.idEmpresa()));
            sucursal.setEmpresa(nuevaEmpresa);
        }

        sucursal.setNombre(dto.nombre());
        sucursal.setDireccion(dto.direccion());

        return mapToResponse(sucursal);
    }

    @Override
    public void eliminar(Long id) {
        if (!sucursalRepository.existsById(id)) {
            throw new RuntimeException("Sucursal no encontrada");
        }
        sucursalRepository.deleteById(id);
    }

    @Override
    public void restaurar(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
    
        sucursal.setDeletedAt(null);
        sucursalRepository.save(sucursal);
    }

    private SucursalResponseDTO mapToResponse(Sucursal s) {
        return new SucursalResponseDTO(
            s.getId(),
            s.getCreatedAt(),
            s.getUpdatedAt(),
            s.getDeletedAt(),
            s.getNombre(),
            s.getDireccion(),
            s.getEmpresa().getRazonSocial()
        );
    }
}