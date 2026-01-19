package com.togamma.heladeria.model.venta;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.togamma.heladeria.model.BaseEntity;
import com.togamma.heladeria.model.seguridad.Sucursal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@SQLDelete(sql = "UPDATE venta SET deleted_at = NOW() where id = ?")
public class Venta extends BaseEntity {
    
    @Enumerated(EnumType.STRING)
    private EstadoVenta estado;
    
    private Double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

}
