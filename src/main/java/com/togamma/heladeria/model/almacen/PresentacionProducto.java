package com.togamma.heladeria.model.almacen;

import org.hibernate.annotations.SQLDelete;

import com.togamma.heladeria.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@SQLDelete(sql = "UPDATE presentacion_producto SET deleted_at = NOW() where id = ?")
public class PresentacionProducto extends BaseEntity {

    private String nombre;
    private Integer factor; //a cuantas unidades base del producto equivale

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;
    
}
