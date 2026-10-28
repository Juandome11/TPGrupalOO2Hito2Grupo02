package com.unla.epicentrogourmet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.helpers.CodigoUnidadHelper;
import com.unla.epicentrogourmet.helpers.ValidacionHelper;

class ValidacionesTest {

	@Test
	void codigoValidoTieneDiezCaracteresYPrefijoSegunElTipo() {
		assertThat(CodigoUnidadHelper.esValido("FT-1234567", true)).isTrue();
		assertThat(CodigoUnidadHelper.esValido("PD-1234567", false)).isTrue();
		assertThat("FT-1234567").hasSize(10);
	}

	@Test
	void codigoConPrefijoDeOtroTipoOFormatoIncorrectoEsInvalido() {
		assertThat(CodigoUnidadHelper.esValido("PD-1234567", true)).isFalse();
		assertThat(CodigoUnidadHelper.esValido("FT-123456", true)).isFalse();
		assertThat(CodigoUnidadHelper.esValido("FT1234567A", true)).isFalse();
		assertThat(CodigoUnidadHelper.esValido(null, true)).isFalse();
	}

	@Test
	void unMenorDeEdadNoPuedeSerPersonal() {
		LocalDate hace17Anios = LocalDate.now().minusYears(17);
		assertThatThrownBy(() -> ValidacionHelper.validarMayorDeEdad(hace17Anios)).isInstanceOf(NegocioException.class);
	}

	@Test
	void unMayorDeEdadEsValido() {
		ValidacionHelper.validarMayorDeEdad(LocalDate.now().minusYears(18));
	}

	@Test
	void laFechaDeIngresoNoPuedeSerFutura() {
		LocalDate nacimiento = LocalDate.now().minusYears(30);
		assertThatThrownBy(() -> ValidacionHelper.validarFechaIngreso(LocalDate.now().plusDays(1), nacimiento))
				.isInstanceOf(NegocioException.class);
	}

	@Test
	void unDniValidoTieneSieteOOchoDigitos() {
		ValidacionHelper.validarDni("1234567");
		ValidacionHelper.validarDni("30444555");
	}

	@Test
	void unDniConLetrasPuntosEspaciosOLargoIncorrectoEsInvalido() {
		for (String dni : new String[] { "123456", "123456789", "12.345.678", "1234567A", " 1234567", "", null }) {
			assertThatThrownBy(() -> ValidacionHelper.validarDni(dni)).as("DNI %s", dni)
					.isInstanceOf(NegocioException.class).hasMessageContaining("7 u 8 dígitos");
		}
	}

	@Test
	void laPatenteAceptaElFormatoMercosurYElAnterior() {
		ValidacionHelper.validarPatente("AB123CD");
		ValidacionHelper.validarPatente("ABC123");
	}

	@Test
	void unaPatenteConOtroFormatoEsInvalida() {
		for (String patente : new String[] { "A1B2C3D", "12ABC34", "ABCD123", "AB1234C", "ab123cd", "", null }) {
			assertThatThrownBy(() -> ValidacionHelper.validarPatente(patente)).as("patente %s", patente)
					.isInstanceOf(NegocioException.class).hasMessageContaining("AB123CD o ABC123");
		}
	}

	@Test
	void laPatenteSeNormalizaSacandoEspaciosYGuionesYPasandoAMayusculas() {
		assertThat(ValidacionHelper.normalizarPatente(" ab 123-cd ")).isEqualTo("AB123CD");
		assertThat(ValidacionHelper.normalizarPatente("abc 123")).isEqualTo("ABC123");
		assertThat(ValidacionHelper.normalizarPatente(null)).isNull();
	}
}
