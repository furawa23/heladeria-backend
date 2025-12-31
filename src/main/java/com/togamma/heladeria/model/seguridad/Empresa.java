package com.togamma.heladeria.model.seguridad;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.togamma.heladeria.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL) 
    private List<Sucursal> sucursales = new ArrayList<>();
    
}
