package com.unla.epicentrogourmet;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.unla.epicentrogourmet.dtos.ReciboSueldoDTO;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.Cocinero;
import com.unla.epicentrogourmet.entities.Costo;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.FoodTrack;
import com.unla.epicentrogourmet.entities.RolCocina;
import com.unla.epicentrogourmet.entities.Turno;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.services.implementation.LiquidacionService;

class LiquidacionServiceTest {

	private final LiquidacionService service = new LiquidacionService();

	private Festival festival(LocalDate desde, LocalDate hasta) {
		Festival festival = new Festival("Festival de prueba", "Verano", desde, hasta);
		Costo costo = new Costo();
		costo.setFestival(festival);
		costo.setCostoPorSuperficie(new BigDecimal("500"));
		costo.setCostoPorMontaje(new BigDecimal("10"));
		costo.setPlusElectricidad(new BigDecimal("2000"));
		costo.setSueldoBase(new BigDecimal("100000"));
		costo.setMontoAnioAntiguedad(new BigDecimal("5000"));
		costo.setPlusCocinero(new BigDecimal("150000"));
		costo.setPlusCocineroAyudante(new BigDecimal("100000"));
		costo.setPlusLavaplatos(new BigDecimal("50000"));
		festival.setCosto(costo);
		return festival;
	}

	private UnidadVenta unidadEn(Festival festival) {
		FoodTrack unidad = new FoodTrack();
		unidad.setNombreComercial("Unidad de prueba");
		unidad.setFestival(festival);
		return unidad;
	}

	private Cocinero cocinero(RolCocina rol) {
		Cocinero cocinero = new Cocinero();
		cocinero.setNombre("Ana");
		cocinero.setApellido("Perez");
		cocinero.setDni("1");
		cocinero.setFechaIngreso(LocalDate.of(2020, 1, 1));
		cocinero.setRolCocina(rol);
		return cocinero;
	}

	@Test
	void ejemploDeLaConsignaLavaplatosFestivalDe38Dias() {
		Festival f = festival(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 7));
		assertThat(f.getDuracionDias()).isEqualTo(38);

		ReciboSueldoDTO recibo = service.calcular(cocinero(RolCocina.LAVAPLATOS), unidadEn(f));

		assertThat(recibo.getTotalACobrar()).isEqualByComparingTo("225000");
	}

	@Test
	void ejemploDeLaConsignaCajeroConTresAniosFestivalDe52Dias() {
		Festival f = festival(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 21));
		assertThat(f.getDuracionDias()).isEqualTo(52);
		Cajero cajero = new Cajero();
		cajero.setNombre("Juan");
		cajero.setApellido("Lopez");
		cajero.setDni("2");
		cajero.setTurno(Turno.NOCHE);
		cajero.setFechaIngreso(LocalDate.of(2022, 12, 1));

		ReciboSueldoDTO recibo = service.calcular(cajero, unidadEn(f));

		assertThat(recibo.getTotalACobrar()).isEqualByComparingTo("230000");
	}

	@Test
	void hastaQuinceDiasCobraElCincuentaPorCiento() {
		Festival f = festival(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 15));

		ReciboSueldoDTO recibo = service.calcular(cocinero(RolCocina.COCINERO), unidadEn(f));

		assertThat(recibo.getTotalACobrar()).isEqualByComparingTo("125000"); // 50% de 250.000
	}

	@Test
	void deDieciseisATreintaDiasCobraElCienPorCiento() {
		Festival f = festival(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 30));

		ReciboSueldoDTO recibo = service.calcular(cocinero(RolCocina.COCINERO_AYUDANTE), unidadEn(f));

		assertThat(f.getDuracionDias()).isEqualTo(30);
		assertThat(recibo.getTotalACobrar()).isEqualByComparingTo("200000"); // 100% de 200.000
	}
}
