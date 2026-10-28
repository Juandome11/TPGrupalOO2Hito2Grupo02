package com.unla.epicentrogourmet.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Estado de la caja del cajero en el día: lo recaudado por su unidad y si ya la cerró. No se persiste. */
@Getter @AllArgsConstructor
public class ResumenCajaDTO {

	private final LocalDate fecha;
	private final String unidad;
	private final BigDecimal total;
	private final boolean cerrada;
	/** Momento del cierre; null si la caja todavía está abierta. */
	private final LocalDateTime cerradaEn;
}
