package com.togamma.heladeria.repository.compra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.togamma.heladeria.model.compra.DetalleCompra;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {

}
