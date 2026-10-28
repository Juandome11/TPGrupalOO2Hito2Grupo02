package com.unla.epicentrogourmet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import com.unla.epicentrogourmet.dtos.ResumenCajaDTO;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.CierreCaja;
import com.unla.epicentrogourmet.entities.Cocinero;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.FoodTrack;
import com.unla.epicentrogourmet.entities.RolCocina;
import com.unla.epicentrogourmet.entities.Turno;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.repositories.ICierreCajaRepository;
import com.unla.epicentrogourmet.repositories.IDetallePedidoRepository;
import com.unla.epicentrogourmet.services.implementation.CierreCajaService;

class CierreCajaServiceTest {

	private final ICierreCajaRepository cierreRepository = mock(ICierreCajaRepository.class);
	private final IDetallePedidoRepository detalleRepository = mock(IDetallePedidoRepository.class);
	private final CierreCajaService service = new CierreCajaService(cierreRepository, detalleRepository);

	private FoodTrack unidad() {
		Festival festival = new Festival("Primavera", "Primavera", LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
		festival.setId(1L);
		FoodTrack unidad = new FoodTrack();
		unidad.setId(10L);
		unidad.setNombreComercial("Parrilla Don Mario");
		unidad.setFestival(festival);
		return unidad;
	}

	private Cajero cajero(FoodTrack unidad) {
		Cajero cajero = new Cajero();
		cajero.setId(6L);
		cajero.setNombre("Marcos");
		cajero.setApellido("Diaz");
		cajero.setTurno(Turno.MANANA);
		cajero.setUnidadVenta(unidad);
		return cajero;
	}

	@Test
	void cerrarCajaRegistraLoRecaudadoPorLaUnidadEnElDia() {
		FoodTrack unidad = unidad();
		Cajero cajero = cajero(unidad);
		LocalDate hoy = LocalDate.now();
		when(cierreRepository.findByCajeroIdAndFecha(6L, hoy)).thenReturn(Optional.empty());
		when(detalleRepository.sumarRecaudacionEntre(10L, 1L, hoy, hoy)).thenReturn(new BigDecimal("34500"));
		when(cierreRepository.saveAndFlush(any(CierreCaja.class))).thenAnswer(i -> i.getArgument(0));

		CierreCaja cierre = service.cerrar(cajero);

		assertThat(cierre.getTotal()).isEqualByComparingTo("34500");
		assertThat(cierre.getFecha()).isEqualTo(hoy);
		assertThat(cierre.getCajero()).isSameAs(cajero);
		assertThat(cierre.getUnidadVenta()).isSameAs(unidad);
	}

	@Test
	void sinVentasEnElDiaElCierreEsPorCero() {
		FoodTrack unidad = unidad();
		when(cierreRepository.findByCajeroIdAndFecha(anyLong(), any())).thenReturn(Optional.empty());
		when(detalleRepository.sumarRecaudacionEntre(anyLong(), anyLong(), any(), any())).thenReturn(null);
		when(cierreRepository.saveAndFlush(any(CierreCaja.class))).thenAnswer(i -> i.getArgument(0));

		assertThat(service.cerrar(cajero(unidad)).getTotal()).isEqualByComparingTo("0");
	}

	@Test
	void noSePuedeCerrarLaCajaDosVecesElMismoDia() {
		Cajero cajero = cajero(unidad());
		when(cierreRepository.findByCajeroIdAndFecha(6L, LocalDate.now()))
				.thenReturn(Optional.of(new CierreCaja(cajero, cajero.getUnidadVenta(), LocalDate.now(), BigDecimal.TEN)));

		assertThatThrownBy(() -> service.cerrar(cajero)).isInstanceOf(NegocioException.class)
				.hasMessageContaining("Ya cerraste la caja de hoy");
		verify(cierreRepository, never()).saveAndFlush(any());
	}

	@Test
	void siLaRestriccionUnicaSaltaPorUnDobleClickTambienEsErrorDeNegocio() {
		Cajero cajero = cajero(unidad());
		when(cierreRepository.findByCajeroIdAndFecha(anyLong(), any())).thenReturn(Optional.empty());
		when(cierreRepository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("duplicado"));

		assertThatThrownBy(() -> service.cerrar(cajero)).isInstanceOf(NegocioException.class)
				.hasMessageContaining("Ya cerraste la caja de hoy");
	}

	@Test
	void soloLosCajerosPuedenCerrarCaja() {
		Cocinero cocinero = new Cocinero();
		cocinero.setRolCocina(RolCocina.COCINERO);
		cocinero.setUnidadVenta(unidad());

		assertThatThrownBy(() -> service.cerrar(cocinero)).isInstanceOf(NegocioException.class)
				.hasMessageContaining("Solo los cajeros");
		verify(cierreRepository, never()).saveAndFlush(any());
	}

	@Test
	void unCajeroSinUnidadNoPuedeCerrarCaja() {
		Cajero cajero = cajero(null);

		assertThatThrownBy(() -> service.cerrar(cajero)).isInstanceOf(NegocioException.class)
				.hasMessageContaining("unidad de venta");
	}

	@Test
	void elResumenMuestraLoRecaudadoMientrasLaCajaEstaAbierta() {
		FoodTrack unidad = unidad();
		LocalDate hoy = LocalDate.now();
		when(cierreRepository.findByCajeroIdAndFecha(6L, hoy)).thenReturn(Optional.empty());
		when(detalleRepository.sumarRecaudacionEntre(eq(10L), eq(1L), eq(hoy), eq(hoy))).thenReturn(new BigDecimal("8000"));

		ResumenCajaDTO resumen = service.resumenDelDia(cajero(unidad));

		assertThat(resumen.isCerrada()).isFalse();
		assertThat(resumen.getTotal()).isEqualByComparingTo("8000");
		assertThat(resumen.getUnidad()).isEqualTo("Parrilla Don Mario");
		assertThat(resumen.getCerradaEn()).isNull();
	}

	@Test
	void despuesDeCerrarElResumenMuestraElTotalFijadoAunqueSeCarguenMasPedidos() {
		FoodTrack unidad = unidad();
		Cajero cajero = cajero(unidad);
		when(cierreRepository.findByCajeroIdAndFecha(6L, LocalDate.now()))
				.thenReturn(Optional.of(new CierreCaja(cajero, unidad, LocalDate.now(), new BigDecimal("5000"))));
		when(detalleRepository.sumarRecaudacionEntre(anyLong(), anyLong(), any(), any())).thenReturn(new BigDecimal("9999"));

		ResumenCajaDTO resumen = service.resumenDelDia(cajero);

		assertThat(resumen.isCerrada()).isTrue();
		assertThat(resumen.getTotal()).isEqualByComparingTo("5000");
		assertThat(resumen.getCerradaEn()).isNotNull();
	}
}
