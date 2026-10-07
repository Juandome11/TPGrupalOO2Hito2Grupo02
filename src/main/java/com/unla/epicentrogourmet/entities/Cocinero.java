package com.unla.epicentrogourmet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cocinero extends Staff {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RolCocina rolCocina;

	@Override
	public String getTipo() {
		return "Cocina - " + rolCocina.getDescripcion();
	}
}
