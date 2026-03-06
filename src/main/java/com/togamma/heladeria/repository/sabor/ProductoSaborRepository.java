package com.togamma.heladeria.repository.sabor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.togamma.heladeria.model.sabores.ProductoSabor;

public interface ProductoSaborRepository  extends JpaRepository<ProductoSabor, Long>{

    List<ProductoSabor> findByProductoId(Long idProducto);

    void deleteByProductoId(Long idProducto);

}
