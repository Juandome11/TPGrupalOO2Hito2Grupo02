package com.unla.epicentrogourmet.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Desarmable extends UnidadVenta {

	@Column(nullable = false)
	private int cantCarpas;

	/** Minutos que toma el montaje. */
	@Column(nullable = false)
	private int tiempoMontaje;

	@Override
	public String getTipo() {
		return "Puesto Desarmable";
	}

	@Override
	public BigDecimal calcularCanon(Costo costo) {
		return canonPorSuperficie(costo).subtract(costo.getCostoPorMontaje().multiply(BigDecimal.valueOf(tiempoMontaje)));
	}
}
