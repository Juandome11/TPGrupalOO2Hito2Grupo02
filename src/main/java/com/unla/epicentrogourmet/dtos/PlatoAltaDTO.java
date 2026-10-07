package com.unla.epicentrogourmet.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class PlatoAltaDTO {

	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;

	@NotNull(message = "El precio de venta es obligatorio")
	@Positive(message = "El precio de venta debe ser mayor a 0")
	private BigDecimal precioVenta;

	@NotNull(message = "El costo de producción es obligatorio")
	@PositiveOrZero(message = "El costo de producción no puede ser negativo")
	private BigDecimal costoProduccion;
}
