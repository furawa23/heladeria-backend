package com.togamma.heladeria.model.venta;

import org.hibernate.annotations.SQLDelete;

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
@SQLDelete(sql = "UPDATE detalle_venta SET deleted_at = NOW() where id = ?")
public class DetalleVenta extends BaseEntity {

    private Integer cantidad;
    private Long subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    //no obligatorio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_presentacion")
    private PresentacionProducto presentacion;

}
