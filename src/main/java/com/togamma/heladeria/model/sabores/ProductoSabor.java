package com.togamma.heladeria.model.sabores;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.togamma.heladeria.model.BaseEntity;
import com.togamma.heladeria.model.almacen.Producto;

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
@SQLDelete(sql = "UPDATE producto_sabor SET deleted_at = NOW() where id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ProductoSabor extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Sabor sabor;
}
