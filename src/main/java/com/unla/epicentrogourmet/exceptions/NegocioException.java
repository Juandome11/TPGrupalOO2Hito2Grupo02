package com.unla.epicentrogourmet.exceptions;

/** Error de regla de negocio o validación de carga: el mensaje se le muestra al usuario. */
public class NegocioException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NegocioException(String message) {
		super(message);
	}
}
