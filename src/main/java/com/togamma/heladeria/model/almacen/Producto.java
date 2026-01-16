package com.togamma.heladeria.model.almacen;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.togamma.heladeria.model.BaseEntity;
import com.togamma.heladeria.model.seguridad.Empresa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@SQLDelete(sql = "UPDATE producto SET deleted_at = NOW() where id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Producto extends BaseEntity {

    private String nombre;
    private Boolean seVende;
    private Long precioUnitarioVenta;
    private String unidadBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private CategoriaProducto categoria;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<RecetaItem> receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

}