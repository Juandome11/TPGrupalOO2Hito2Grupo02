package com.unla.epicentrogourmet.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatoRankingDTO {
    private Long id;
    private String nombre;
    private BigDecimal precioVenta;
    private BigDecimal costoProduccion;
    private Long cantidadVendida;
    private BigDecimal recaudacionTotal;
}