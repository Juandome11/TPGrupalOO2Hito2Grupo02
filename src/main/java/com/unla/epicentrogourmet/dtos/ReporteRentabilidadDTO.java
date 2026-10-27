package com.unla.epicentrogourmet.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ReporteRentabilidadDTO {
    private final String festivalNombre;
    private final String unidadNombre;
    private final LocalDate desde;
    private final LocalDate hasta;
    private final BigDecimal recaudacion;
    private final BigDecimal costoPlatos;
    private final BigDecimal sueldos;
    private final BigDecimal canon;

    public BigDecimal getGananciaBruta() {
        return recaudacion.subtract(costoPlatos);
    }

    public BigDecimal getRentabilidadNeta() {
        return getGananciaBruta().subtract(sueldos).subtract(canon);
    }
}