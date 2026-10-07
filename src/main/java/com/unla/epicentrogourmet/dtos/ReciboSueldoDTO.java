package com.unla.epicentrogourmet.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Liquidación de haberes de un empleado en un festival. No se persiste; alimenta la vista y el PDF. */
@Getter @AllArgsConstructor
public class ReciboSueldoDTO {

	private final String empleado;
	private final String dni;
	private final String tipoEmpleado;
	private final String festival;
	private final String unidad;
	private final LocalDate festivalDesde;
	private final LocalDate festivalHasta;
	private final long diasFestival;
	private final BigDecimal sueldoBase;
	/** Plus por categoría (cocina) o monto por antigüedad (resto del personal). */
	private final String conceptoAdicional;
	private final BigDecimal montoAdicional;
	private final BigDecimal porcentajeAplicado;
	private final BigDecimal totalACobrar;
}
