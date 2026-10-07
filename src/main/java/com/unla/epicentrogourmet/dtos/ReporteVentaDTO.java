package com.unla.epicentrogourmet.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Reporte de recaudación de una unidad en un festival. No se persiste. */
@Getter @AllArgsConstructor
public class ReporteVentaDTO {

	private final String festivalNombre;
	private final String unidadNombre;
	private final BigDecimal recaudacionTotal;
}
