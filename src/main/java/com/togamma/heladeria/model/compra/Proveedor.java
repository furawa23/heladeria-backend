package com.togamma.heladeria.model.compra;

import org.hibernate.annotations.SQLDelete;

import com.togamma.heladeria.model.BaseEntity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@SQLDelete(sql = "UPDATE proveedor SET deleted_at = NOW() where id = ?")
public class Proveedor extends BaseEntity {

    private String razonSocial;
    private String ruc;
    private String telefonoContacto;
    
}
