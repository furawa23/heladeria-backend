package com.togamma.heladeria.model.caja;

import java.time.LocalDateTime;

import com.togamma.heladeria.model.BaseEntity;
import com.togamma.heladeria.model.seguridad.Sucursal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class Caja extends BaseEntity {

    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private Double montoInicial;
    private Double montoFinal;

    @Enumerated(EnumType.STRING)
    private EstadoCaja estado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal")
    private Sucursal sucursal;
}
