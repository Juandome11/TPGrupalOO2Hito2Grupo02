package com.unla.epicentrogourmet;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.unla.epicentrogourmet.entities.Costo;
import com.unla.epicentrogourmet.entities.Desarmable;
import com.unla.epicentrogourmet.entities.FoodTrack;

class CanonTest {

	private Costo costo() {
		Costo costo = new Costo();
		costo.setCostoPorSuperficie(new BigDecimal("500"));
		costo.setCostoPorMontaje(new BigDecimal("10"));
		costo.setPlusElectricidad(new BigDecimal("2000"));
		return costo;
	}

	@Test
	void foodTruckConConexionElectricaSumaElPlus() {
		FoodTrack foodTrack = new FoodTrack();
		foodTrack.setSuperficie(12);
		foodTrack.setRequiereConexionElectrica(true);

		assertThat(foodTrack.calcularCanon(costo())).isEqualByComparingTo("8000"); // 12 * 500 + 2000
	}

	@Test
	void foodTruckSinConexionElectricaNoPagaPlus() {
		FoodTrack foodTrack = new FoodTrack();
		foodTrack.setSuperficie(12);
		foodTrack.setRequiereConexionElectrica(false);

		assertThat(foodTrack.calcularCanon(costo())).isEqualByComparingTo("6000");
	}

	@Test
	void puestoDesarmableDescuentaElTiempoDeMontaje() {
		Desarmable puesto = new Desarmable();
		puesto.setSuperficie(20);
		puesto.setTiempoMontaje(90);

		assertThat(puesto.calcularCanon(costo())).isEqualByComparingTo("9100"); // 20 * 500 - 90 * 10
	}
}
