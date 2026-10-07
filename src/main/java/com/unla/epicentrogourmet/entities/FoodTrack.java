package com.unla.epicentrogourmet.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class FoodTrack extends UnidadVenta {

	@Column(nullable = false)
	private String patente;

	@Column(nullable = false)
	private boolean requiereConexionElectrica;

	@Override
	public String getTipo() {
		return "Food Truck";
	}

	@Override
	public BigDecimal calcularCanon(Costo costo) {
		BigDecimal canon = canonPorSuperficie(costo);
		return requiereConexionElectrica ? canon.add(costo.getPlusElectricidad()) : canon;
	}
}
