package com.togamma.heladeria.model.seguridad;

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
@SQLDelete(sql = "UPDATE sucursal SET deleted_at = NOW() where id = ?")
public class Sucursal extends BaseEntity {

    private String nombre;
    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;
}
