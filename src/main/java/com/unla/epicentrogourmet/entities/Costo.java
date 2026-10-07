package com.unla.epicentrogourmet.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Valores de cálculo configurables por Festival (canon y liquidación de haberes).
 * Evita hardcodear sueldo base, plus por categoría y constantes del canon en los métodos.
 */
@Entity
@Getter @Setter @NoArgsConstructor
public class Costo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "festival_id", nullable = false, unique = true)
	private Festival festival;

	@Column(nullable = false)
	private BigDecimal costoPorSuperficie;

	@Column(nullable = false)
	private BigDecimal costoPorMontaje;

	@Column(nullable = false)
	private BigDecimal plusElectricidad;

	@Column(nullable = false)
	private BigDecimal sueldoBase;

	@Column(nullable = false)
	private BigDecimal montoAnioAntiguedad;

	@Column(nullable = false)
	private BigDecimal plusCocinero;

	@Column(nullable = false)
	private BigDecimal plusCocineroAyudante;

	@Column(nullable = false)
	private BigDecimal plusLavaplatos;

	public BigDecimal plusPara(RolCocina rol) {
		return switch (rol) {
			case COCINERO -> plusCocinero;
			case COCINERO_AYUDANTE -> plusCocineroAyudante;
			case LAVAPLATOS -> plusLavaplatos;
		};
	}
}
