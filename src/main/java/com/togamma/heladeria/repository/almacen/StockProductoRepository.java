package com.togamma.heladeria.repository.almacen;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.togamma.heladeria.model.almacen.StockProducto;

public interface StockProductoRepository extends JpaRepository<StockProducto, Long> {
    
    @EntityGraph(attributePaths = {"producto","sucursal"})
    Optional<StockProducto> findByProductoIdAndSucursalId(Long productoId, Long sucursalId);
    Boolean existsByProductoIdAndSucursalId(Long idProducto, Long idSucursal);
    Boolean existsByIdAndSucursalEmpresaId(Long id, Long idEmpresa);
    @EntityGraph(attributePaths = {"producto","sucursal"})
    List<StockProducto> findByProductoId(Long idProducto);
}