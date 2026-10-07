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
}
