package com.unla.epicentrogourmet.entities;

import java.time.LocalDate;
import java.time.Period;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Personal del predio. Ya no es abstracta: un Staff "puro" (sin ser Cocinero ni Cajero)
 * es el Responsable de una Unidad de Venta.
 */
@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Staff {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private String apellido;

	@Column(nullable = false, unique = true)
	private String dni;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private LocalDate fechaNacimiento;

	@Column(nullable = false)
	private LocalDate fechaIngreso;

	/** Baja lógica: el registro no se borra, solo se marca. */
	@Column(nullable = false)
	private boolean eliminado = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unidad_venta_id")
	private UnidadVenta unidadVenta;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", unique = true)
	private User user;

	public String getTipo() {
		return "Responsable";
	}

	public String getNombreCompleto() {
		return nombre + " " + apellido;
	}

	public int getEdad() {
		return Period.between(fechaNacimiento, LocalDate.now()).getYears();
	}

	/** Años cumplidos de antigüedad a una fecha dada. */
	public int antiguedadAnios(LocalDate fecha) {
		return Math.max(0, Period.between(fechaIngreso, fecha).getYears());
	}
}
