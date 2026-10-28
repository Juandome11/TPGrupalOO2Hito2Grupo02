package com.unla.epicentrogourmet.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FestivalAltaDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La temporada es obligatoria")
    private String temporada;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    @NotNull(message = "El costo por superficie es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "Debe ser mayor que cero")
    private BigDecimal costoPorSuperficie;

    @NotNull(message = "El costo por montaje es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "Debe ser mayor que cero")
    private BigDecimal costoPorMontaje;

    @NotNull(message = "El plus de electricidad es obligatorio")
    @DecimalMin(value = "0.0", message = "No puede ser negativo")
    private BigDecimal plusElectricidad;

    @NotNull(message = "El sueldo base es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "Debe ser mayor que cero")
    private BigDecimal sueldoBase;

    @NotNull(message = "El monto por año de antigüedad es obligatorio")
    @DecimalMin(value = "0.0", message = "No puede ser negativo")
    private BigDecimal montoAnioAntiguedad;

    @NotNull(message = "El plus de cocinero es obligatorio")
    @DecimalMin(value = "0.0", message = "No puede ser negativo")
    private BigDecimal plusCocinero;

    @NotNull(message = "El plus de cocinero ayudante es obligatorio")
    @DecimalMin(value = "0.0", message = "No puede ser negativo")
    private BigDecimal plusCocineroAyudante;

    @NotNull(message = "El plus de lavaplatos es obligatorio")
    @DecimalMin(value = "0.0", message = "No puede ser negativo")
    private BigDecimal plusLavaplatos;
}
