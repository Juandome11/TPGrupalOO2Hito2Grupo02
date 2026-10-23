package com.unla.epicentrogourmet.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Datos de una unidad para el reporte de mayores cánones. */
@Getter
@AllArgsConstructor
public class ReporteMayoresCanonDTO {
    private final String nombreComercial;
    private final String codigo;
    private final String tipoUnidad;
    private final BigDecimal canon;
}
