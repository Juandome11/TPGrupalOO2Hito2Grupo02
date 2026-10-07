package com.unla.epicentrogourmet.entities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Festival {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private String temporada;

	@Column(nullable = false)
	private LocalDate fechaInicio;

	@Column(nullable = false)
	private LocalDate fechaFin;

	@OneToOne(mappedBy = "festival", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Costo costo;

	@OneToMany(mappedBy = "festival", fetch = FetchType.LAZY)
	private Set<UnidadVenta> unidadesVenta = new HashSet<>();

	public Festival(String nombre, String temporada, LocalDate fechaInicio, LocalDate fechaFin) {
		this.nombre = nombre;
		this.temporada = temporada;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public long getDuracionDias() {
		return ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
	}

	public boolean estaActivo(LocalDate fecha) {
		return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
	}
}
