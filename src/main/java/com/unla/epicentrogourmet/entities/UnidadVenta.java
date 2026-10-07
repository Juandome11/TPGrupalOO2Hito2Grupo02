package com.unla.epicentrogourmet.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UnidadVenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombreComercial;

	@Column(nullable = false)
	private double superficie;

	@Column(nullable = false, unique = true, length = 10)
	private String codigo;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "festival_id")
	private Festival festival;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responsable_id")
	private Staff responsable;

	@OneToMany(mappedBy = "unidadVenta", fetch = FetchType.LAZY)
	private Set<Staff> empleados = new HashSet<>();

	@OneToMany(mappedBy = "unidadVenta", fetch = FetchType.LAZY)
	private Set<Plato> platos = new HashSet<>();

	public abstract String getTipo();

	/** Monto de canon que paga la unidad, según los valores de Costo del festival. */
	public abstract BigDecimal calcularCanon(Costo costo);

	protected BigDecimal canonPorSuperficie(Costo costo) {
		return costo.getCostoPorSuperficie().multiply(BigDecimal.valueOf(superficie));
	}
}
