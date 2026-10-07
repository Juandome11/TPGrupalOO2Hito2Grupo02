package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Formulario público: registra una Unidad de Venta junto con su Responsable (y su usuario). */
@Getter @Setter @NoArgsConstructor
public class RegistroUnidadResponsableDTO {

	public static final String FOOD_TRUCK = "FOOD_TRUCK";
	public static final String DESARMABLE = "DESARMABLE";

	@NotNull(message = "Elegí el festival")
	private Long festivalId;

	@NotBlank(message = "Elegí el tipo de unidad")
	private String tipo;

	@NotBlank(message = "El nombre comercial es obligatorio")
	private String nombreComercial;

	@NotNull(message = "La superficie es obligatoria")
	@Positive(message = "La superficie debe ser mayor a 0")
	private Double superficie;

	@NotBlank(message = "El código es obligatorio")
	@Size(min = 10, max = 10, message = "El código debe tener exactamente 10 caracteres")
	private String codigo;

	// Food Truck
	private String patente;
	private boolean requiereConexionElectrica;

	// Puesto Desarmable
	@Positive(message = "La cantidad de carpas debe ser mayor a 0")
	private Integer cantCarpas;

	@Positive(message = "El tiempo de montaje debe ser mayor a 0")
	private Integer tiempoMontaje;

	// Responsable
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

	// Usuario
	@NotBlank(message = "El usuario es obligatorio")
	@Size(min = 4, max = 100, message = "El usuario debe tener entre 4 y 100 caracteres")
	private String username;

	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 6, max = 50, message = "La contraseña debe tener al menos 6 caracteres")
	private String password;
}
