package com.togamma.heladeria.model.caja;

import com.togamma.heladeria.model.BaseEntity;
import com.togamma.heladeria.model.compra.Compra;
import com.togamma.heladeria.model.venta.Venta;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class MovimientoCaja extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    private Double monto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja")
    private Caja caja;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = true)
    private Venta referenciaVenta;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = true)
    private Compra referenciaCompra;

}
