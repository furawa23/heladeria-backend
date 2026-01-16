package com.togamma.heladeria.service.almacen.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.almacen.CategoriaProdRequestDTO;
import com.togamma.heladeria.dto.response.almacen.CategoriaProdResponseDTO;
import com.togamma.heladeria.model.almacen.CategoriaProducto;
import com.togamma.heladeria.repository.almacen.CategoriaProductoRepository;
import com.togamma.heladeria.service.almacen.CategoriaProductoService;
import com.togamma.heladeria.service.seguridad.ContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaProductoServiceImpl implements CategoriaProductoService {

    private final CategoriaProductoRepository categoriaRepository;
    private final ContextService contexto;

    @Override
    public CategoriaProdResponseDTO crear(CategoriaProdRequestDTO dto) {

        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setNombre(dto.nombre());
        categoria.setEmpresa(contexto.getEmpresaLogueada());

        CategoriaProducto guardada = categoriaRepository.save(categoria);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaProdResponseDTO> listarTodas(Pageable page) {
        return categoriaRepository.findByEmpresaId(contexto.getEmpresaLogueada().getId(), page)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaProdResponseDTO obtenerPorId(Long id) {
        CategoriaProducto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria de Producto no encontrada"));

        return mapToResponse(categoria);
    }

    @Override
    public CategoriaProdResponseDTO actualizar(Long id, CategoriaProdRequestDTO dto) {
    
        CategoriaProducto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria de Producto no encontrada"));

        if (!categoria.getEmpresa().getId().equals(contexto.getEmpresaLogueada().getId())) {
            throw new RuntimeException("No tienes permiso para modificar esta categoría");
        }

        categoria.setNombre(dto.nombre());
    
        CategoriaProducto actualizada = categoriaRepository.save(categoria);
    
        return mapToResponse(actualizada);
    }
    

    @Override
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoria de Producto no encontrada");
        }
        categoriaRepository.deleteById(id);
    }

    private CategoriaProdResponseDTO mapToResponse(CategoriaProducto s) {
        return new CategoriaProdResponseDTO(
            s.getId(),
            s.getCreatedAt(),
            s.getUpdatedAt(),
            s.getDeletedAt(),
            s.getNombre()
        );
    }
}