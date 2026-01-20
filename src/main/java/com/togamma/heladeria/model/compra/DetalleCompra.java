package com.togamma.heladeria.model.compra;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.togamma.heladeria.model.BaseEntity;
import com.togamma.heladeria.model.almacen.PresentacionProducto;
import com.togamma.heladeria.model.almacen.Producto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@SQLDelete(sql = "UPDATE detalle_compra SET deleted_at = NOW() where id = ?")
@SQLRestriction("deleted_at IS NULL")
public class DetalleCompra extends BaseEntity {

    private Integer cantidad;
    private Double precioUnitarioCompra;
    private Double subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra")
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_presentacion")
    private PresentacionProducto presentacion;

}
