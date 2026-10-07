package com.unla.epicentrogourmet.helpers;

import java.util.regex.Pattern;

/**
 * Regla propia del código de una Unidad de Venta: 10 caracteres con formato XX-NNNNNNN,
 * donde XX es FT (Food Truck) o PD (Puesto Desarmable) y NNNNNNN son 7 dígitos.
 */
public final class CodigoUnidadHelper {

	public static final String PREFIJO_FOOD_TRUCK = "FT";
	public static final String PREFIJO_DESARMABLE = "PD";

	private static final Pattern FORMATO = Pattern.compile("^(FT|PD)-\\d{7}$");

	private CodigoUnidadHelper() {
	}

	public static boolean esValido(String codigo, boolean foodTruck) {
		if (codigo == null || !FORMATO.matcher(codigo).matches()) {
			return false;
		}
		String prefijo = foodTruck ? PREFIJO_FOOD_TRUCK : PREFIJO_DESARMABLE;
		return codigo.startsWith(prefijo + "-");
	}
}
