package com.togamma.heladeria.dto.response.caja;

import java.time.LocalDateTime;
import com.togamma.heladeria.model.caja.EstadoCaja;

public record CajaResponseDTO(
    Long id,
    LocalDateTime fechaApertura,
    LocalDateTime fechaCierre,
    Double montoInicial,
    Double montoFinal,
    EstadoCaja estado
) {}