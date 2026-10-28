package com.unla.epicentrogourmet.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
 * Cierre de caja de un cajero al finalizar el día: queda registrado el total que recaudó la unidad de venta
 * en esa fecha. Un cajero solo puede cerrar su caja una vez por día.
 */
@Entity
@Table(name = "cierre_caja", uniqueConstraints = @UniqueConstraint(columnNames = { "cajero_id", "fecha" }))
@Getter @Setter @NoArgsConstructor
public class CierreCaja {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cajero_id")
	private Cajero cajero;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "unidad_venta_id")
	private UnidadVenta unidadVenta;

	@Column(nullable = false)
	private LocalDate fecha;

	@Column(nullable = false, precision = 14, scale = 2)
	private BigDecimal total;

	@Column(nullable = false)
	private LocalDateTime cerradaEn;

	public CierreCaja(Cajero cajero, UnidadVenta unidadVenta, LocalDate fecha, BigDecimal total) {
		this.cajero = cajero;
		this.unidadVenta = unidadVenta;
		this.fecha = fecha;
		this.total = total;
		this.cerradaEn = LocalDateTime.now();
	}
}
