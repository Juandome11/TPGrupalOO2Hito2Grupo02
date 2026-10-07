package com.unla.epicentrogourmet.entities;

public enum Turno {
	MANANA("Mañana"),
	NOCHE("Noche");

	private final String descripcion;

	Turno(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
}
