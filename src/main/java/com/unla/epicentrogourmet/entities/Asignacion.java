package com.unla.epicentrogourmet.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Historial de trabajo de un empleado: cada fila indica que trabajó en una unidad de venta
 * (y por lo tanto en el festival de esa unidad). Staff.unidadVenta guarda solo la unidad actual.
 */
@Entity
@Table(name = "asignacion", uniqueConstraints = @UniqueConstraint(columnNames = { "staff_id", "unidad_venta_id" }))
@Getter @Setter @NoArgsConstructor
public class Asignacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "staff_id")
	private Staff staff;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "unidad_venta_id")
	private UnidadVenta unidadVenta;

	public Asignacion(Staff staff, UnidadVenta unidadVenta) {
		this.staff = staff;
		this.unidadVenta = unidadVenta;
	}

	public Festival getFestival() {
		return unidadVenta.getFestival();
	}
}
