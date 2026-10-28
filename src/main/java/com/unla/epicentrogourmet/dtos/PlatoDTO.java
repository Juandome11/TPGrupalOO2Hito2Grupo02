package com.unla.epicentrogourmet.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlatoDTO {
    private Long id;
    private String nombre;
    private BigDecimal precioVenta;
    private BigDecimal costoProduccion;
    private Long totalUnidadesVendidas; // Útil para mostrar la cantidad acumulada en el reporte
}