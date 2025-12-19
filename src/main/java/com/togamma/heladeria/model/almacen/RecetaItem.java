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
@SQLDelete(sql = "UPDATE receta_item SET deleted_at = NOW() where id = ?")
public class RecetaItem extends BaseEntity {

    private Integer cantidadUsada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insumo")
    private Producto insumo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

}
