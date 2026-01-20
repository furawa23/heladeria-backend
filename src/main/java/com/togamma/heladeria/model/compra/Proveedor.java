package com.togamma.heladeria.model.compra;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
@SQLDelete(sql = "UPDATE proveedor SET deleted_at = NOW() where id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Proveedor extends BaseEntity {

    private String razonSocial;
    private String ruc;
    private String telefonoContacto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;
}
