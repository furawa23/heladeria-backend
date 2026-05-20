package com.togamma.heladeria.model.caja;

import java.time.LocalDateTime;

import com.togamma.heladeria.model.BaseEntity;
import com.togamma.heladeria.model.seguridad.Sucursal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Caja extends BaseEntity {

    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private Double montoInicial;
    private Double montoFinal;

    @Enumerated(EnumType.STRING)
    private EstadoCaja estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal")
    private Sucursal sucursal;
}
