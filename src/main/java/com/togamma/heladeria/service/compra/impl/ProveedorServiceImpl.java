package com.togamma.heladeria.service.compra.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.compra.ProveedorRequestDTO;
import com.togamma.heladeria.dto.response.compra.ProveedorResponseDTO;
import com.togamma.heladeria.model.compra.Proveedor;
import com.togamma.heladeria.repository.compra.ProveedorRepository;
import com.togamma.heladeria.service.compra.ProveedorService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ContextService contexto;

    @Override
    public ProveedorResponseDTO crear(ProveedorRequestDTO dto) {

        if (proveedorRepository.existsByRazonSocialAndEmpresaId(dto.razonSocial(), contexto.getEmpresaLogueada().getId())
            || proveedorRepository.existsByRucAndEmpresaId(dto.ruc(), contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("Proveedor ya registrado anteriormente");
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(dto.razonSocial());
        proveedor.setRuc(dto.ruc());
        proveedor.setTelefonoContacto(dto.telefono());
        proveedor.setEmpresa(contexto.getEmpresaLogueada());

        Proveedor guardada = proveedorRepository.save(proveedor);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProveedorResponseDTO> listarTodas(Pageable page) {
        return proveedorRepository.findByEmpresaId(contexto.getEmpresaLogueada().getId(), page)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponseDTO obtenerPorId(Long id) {
        Proveedor proveedor = proveedorRepository.findByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        return mapToResponse(proveedor);
    }

    @Override
    public ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO dto) {
    
        Proveedor proveedor = proveedorRepository.findByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        if (!proveedor.getRazonSocial().equalsIgnoreCase(dto.razonSocial())) {
            if (proveedorRepository.existsByRazonSocialAndEmpresaId(dto.razonSocial(), contexto.getEmpresaLogueada().getId())
                || proveedorRepository.existsByRucAndEmpresaId(dto.ruc(), contexto.getEmpresaLogueada().getId())) {
                throw new RuntimeException("Ya existe un proveedor igual");
            }
        }
        
        proveedor.setRazonSocial(dto.razonSocial());
        proveedor.setRuc(dto.ruc());
        proveedor.setTelefonoContacto(dto.telefono());
    
        Proveedor actualizada = proveedorRepository.save(proveedor);
        return mapToResponse(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        if (!proveedorRepository.existsByIdAndEmpresaId(id, contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("Proveedor no encontrado");
        }
        proveedorRepository.deleteById(id);
    }

    private ProveedorResponseDTO mapToResponse(Proveedor s) {
        return new ProveedorResponseDTO(
            s.getId(),
            s.getRazonSocial(),
            s.getRuc(),
            s.getTelefonoContacto()
        );
    }
}