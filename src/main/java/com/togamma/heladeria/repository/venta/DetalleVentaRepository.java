package com.togamma.heladeria.repository.venta;

import org.springframework.data.jpa.repository.JpaRepository;
import com.togamma.heladeria.model.venta.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

}
