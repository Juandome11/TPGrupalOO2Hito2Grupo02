package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.unla.epicentrogourmet.entities.RolCocina;
import com.unla.epicentrogourmet.entities.Turno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Formulario de alta y modificación de empleados (Cocinero / Cajero). */
@Getter @Setter @NoArgsConstructor
public class EmpleadoDTO {

	public static final String COCINERO = "COCINERO";
	public static final String CAJERO = "CAJERO";

	private Long id;

	@NotBlank(message = "Elegí el tipo de empleado")
	private String tipo;

	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;

	@NotBlank(message = "El apellido es obligatorio")
	private String apellido;

	@NotBlank(message = "El DNI es obligatorio")
	private String dni;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El email no es válido")
	private String email;

	@NotNull(message = "La fecha de nacimiento es obligatoria")
	@Past(message = "La fecha de nacimiento debe ser pasada")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaNacimiento;

	@NotNull(message = "La fecha de ingreso es obligatoria")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaIngreso;

	private RolCocina rolCocina;

	private Turno turno;
}
