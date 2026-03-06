package com.togamma.heladeria.repository.almacen;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import com.togamma.heladeria.model.almacen.Producto;
import com.togamma.heladeria.repository.EmpresaScopedRepository;

public interface ProductoRepository extends EmpresaScopedRepository<Producto> {
    
    @EntityGraph(attributePaths = {"categoria", "receta.insumo"})
    Page<Producto> findByEmpresaIdAndSeVendeTrue(Long id, Pageable pageable);
    
    @EntityGraph(attributePaths = {"categoria", "receta.insumo"})
    Page<Producto> findByEmpresaIdAndSeVendeFalse(Long id, Pageable pageable);

    @EntityGraph(attributePaths = {"categoria"})
    Page<Producto> findByEmpresaIdAndRecetaIsEmpty(Long id, Pageable pageable);
    
    Boolean existsByNombreAndEmpresaId(String nombre, Long id);
    
    @EntityGraph(attributePaths = {"categoria", "receta.insumo"})
    Page<Producto> findByCategoriaIdAndEmpresaId(Long idCategoria, Long idEmpresa, Pageable pageable);
    
    @Override
    @EntityGraph(attributePaths = {"categoria", "receta.insumo"})
    Page<Producto> findByEmpresaId(Long idEmpresa, Pageable pageable);
    
    @Override
    @EntityGraph(attributePaths = {"categoria", "receta.insumo"})
    Optional<Producto> findByIdAndEmpresaId(Long id, Long idEmpresa);
    
    List<Producto> findByIdInAndEmpresaId(List<Long> idsInsumo, Long idEmpresa);
    
    @EntityGraph(attributePaths = {"categoria", "receta.insumo"})
    List<Producto> findByEmpresaIdAndSeVendeTrue(Long empresaId);

    boolean existsByNombreAndEmpresaIdAndIdNot(String nombre, Long idEmpresa, Long id);
}