package com.togamma.heladeria.model.venta;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.togamma.heladeria.model.BaseEntity;
import com.togamma.heladeria.model.sabores.Sabor;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE detalle_venta_sabor SET deleted_at = NOW() where id = ?")
@SQLRestriction("deleted_at IS NULL")
public class DetalleVentaSabor extends BaseEntity {

    private Double precioAdicionalAplicado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_venta")
    private DetalleVenta detalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sabor")
    private Sabor sabor;

}
