package com.unla.epicentrogourmet.entities;

public enum RolCocina {
	COCINERO("Cocinero"),
	COCINERO_AYUDANTE("Cocinero ayudante"),
	LAVAPLATOS("Lavaplatos");

	private final String descripcion;

	RolCocina(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
}
