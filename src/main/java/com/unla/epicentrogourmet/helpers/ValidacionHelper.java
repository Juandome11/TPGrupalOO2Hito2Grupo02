package com.unla.epicentrogourmet.helpers;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

import com.unla.epicentrogourmet.exceptions.NegocioException;

/** Validaciones de carga compartidas entre servicios. */
public final class ValidacionHelper {

	public static final int EDAD_MINIMA = 18;
	/** DNI argentino: solo dígitos, 7 u 8. */
	private static final Pattern DNI = Pattern.compile("^\\d{7,8}$");
	/** Patente argentina: formato Mercosur (AB123CD) o anterior (ABC123). */
	private static final Pattern PATENTE = Pattern.compile("^([A-Z]{2}\\d{3}[A-Z]{2}|[A-Z]{3}\\d{3})$");

	private ValidacionHelper() {
	}

	public static void validarDni(String dni) {
		if (dni == null || !DNI.matcher(dni).matches()) {
			throw new NegocioException("El DNI debe tener 7 u 8 dígitos, solo números (sin puntos ni espacios).");
		}
	}

	/** Saca espacios y guiones y pasa a mayúsculas, para que "ab 123-cd" se guarde como AB123CD. */
	public static String normalizarPatente(String patente) {
		return patente == null ? null : patente.replaceAll("[\\s-]", "").toUpperCase();
	}

	/** Valida una patente ya normalizada (ver normalizarPatente). */
	public static void validarPatente(String patenteNormalizada) {
		if (patenteNormalizada == null || !PATENTE.matcher(patenteNormalizada).matches()) {
			throw new NegocioException("La patente no es válida: usá el formato AB123CD o ABC123.");
		}
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
