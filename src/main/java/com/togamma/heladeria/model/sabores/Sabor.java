package com.togamma.heladeria.model.sabores;

import org.hibernate.annotations.SQLDelete;

import com.togamma.heladeria.model.BaseEntity;
import com.togamma.heladeria.model.seguridad.Empresa;

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
@SQLDelete(sql = "UPDATE sabor SET deleted_at = NOW() where id = ?")
public class Sabor extends BaseEntity {

    private String nombre;
    private Double precioAdicional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

}
