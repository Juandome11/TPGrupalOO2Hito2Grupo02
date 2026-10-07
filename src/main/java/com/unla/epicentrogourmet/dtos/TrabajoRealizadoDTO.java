package com.unla.epicentrogourmet.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Una fila del historial laboral del empleado: dónde trabajó y cuánto cobró (recibo null si el festival no tiene costos). */
@Getter @AllArgsConstructor
public class TrabajoRealizadoDTO {

	private final Long asignacionId;
	private final boolean festivalActivo;
	private final boolean unidadActual;
	private final ReciboSueldoDTO recibo;
	private final String festival;
	private final String unidad;
}
