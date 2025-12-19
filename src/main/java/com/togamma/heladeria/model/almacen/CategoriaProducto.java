package com.togamma.heladeria.model.almacen;

import org.hibernate.annotations.SQLDelete;

import com.togamma.heladeria.model.BaseEntity;
import com.togamma.heladeria.model.seguridad.Empresa;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@SQLDelete(sql = "UPDATE categoria_producto SET deleted_at = NOW() where id = ?")
public class CategoriaProducto extends BaseEntity {

    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;
    
}
