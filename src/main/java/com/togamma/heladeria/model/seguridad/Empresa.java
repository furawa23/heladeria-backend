package com.togamma.heladeria.model.seguridad;

import org.hibernate.annotations.SQLDelete;

import com.togamma.heladeria.model.BaseEntity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@SQLDelete(sql = "UPDATE empresa SET deleted_at = NOW() where id = ?")
public class Empresa extends BaseEntity {

    private String ruc;
    private String razonSocial;
    private String nombreDuenio;
    private String telefono;
    
}
