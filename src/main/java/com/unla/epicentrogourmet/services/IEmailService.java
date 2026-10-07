package com.unla.epicentrogourmet.services;

public interface IEmailService {

	/** Devuelve false si el mail no se pudo enviar (no lanza excepción). */
	boolean enviarCredencialesEmpleado(String destinatario, String nombre, String username, String password);
}
