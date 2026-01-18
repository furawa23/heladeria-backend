package com.togamma.heladeria.repository.almacen;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.togamma.heladeria.model.almacen.PresentacionProducto;

public interface PresentacionProductoRepository extends JpaRepository<PresentacionProducto, Long> {
    
    @EntityGraph(attributePaths = {"producto"})
    Page<PresentacionProducto> findByProductoId(Long idProducto, Pageable pageable);
    Boolean existsByNombreAndProductoId(String nombre, Long id);
    @EntityGraph(attributePaths = {"producto"})
    Optional<PresentacionProducto> findByIdAndEmpresaId(Long id, Long idEmpresa);
    Boolean existsByIdAndEmpresaId(Long id, Long idEmpresa);
}
