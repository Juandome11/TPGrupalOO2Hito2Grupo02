package com.unla.epicentrogourmet.helpers;

import java.time.LocalDate;
import java.time.Period;

import com.unla.epicentrogourmet.exceptions.NegocioException;

/** Validaciones de carga compartidas entre servicios. */
public final class ValidacionHelper {

	public static final int EDAD_MINIMA = 18;

	private ValidacionHelper() {
	}

	public static void validarMayorDeEdad(LocalDate fechaNacimiento) {
		if (Period.between(fechaNacimiento, LocalDate.now()).getYears() < EDAD_MINIMA) {
			throw new NegocioException("El personal debe ser mayor de edad (" + EDAD_MINIMA + " años o más).");
		}
	}

	public static void validarFechaIngreso(LocalDate fechaIngreso, LocalDate fechaNacimiento) {
		if (fechaIngreso.isAfter(LocalDate.now())) {
			throw new NegocioException("La fecha de ingreso no puede ser futura.");
		}
		if (fechaIngreso.isBefore(fechaNacimiento.plusYears(EDAD_MINIMA))) {
			throw new NegocioException("La fecha de ingreso es anterior a que la persona cumpliera la mayoría de edad.");
		}
	}
}
