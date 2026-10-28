package com.unla.epicentrogourmet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.unla.epicentrogourmet.repositories.IAsignacionRepository;

/**
 * Recorre las pantallas de cada rol contra la base cargada con scripts/datos_prueba.sql.
 * Solo corre si están definidas las variables de entorno de la base (DB_URL, DB_USERNAME, DB_PASSWORD).
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class VistasSmokeTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private IAsignacionRepository asignacionRepository;

	@Test
	void paginasPublicas() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk());
		mvc.perform(get("/login")).andExpect(status().isOk());
		mvc.perform(get("/unidad/registro")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void vistasDelAdministrador() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isOk());
		mvc.perform(get("/admin/festival/1/unidades")).andExpect(status().isOk());
		mvc.perform(get("/admin/festival/nuevo")).andExpect(status().isOk());
		mvc.perform(get("/admin/festival/1/ranking")).andExpect(status().isOk())
				.andExpect(content().string(Matchers.containsString("Parrilla Don Mario")));
		mvc.perform(get("/admin/festival/1/mayores-canon?x=2")).andExpect(status().isOk());
		mvc.perform(get("/admin/festival/1/unidad/1/plato-estrella")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "resp.parrilla", roles = "RESPONSABLE")
	void vistasDelResponsable() throws Exception {
		mvc.perform(get("/responsable")).andExpect(status().isOk())
				.andExpect(content().string(Matchers.containsString("/responsable/pedidos/nuevo")));
		mvc.perform(get("/responsable/empleados")).andExpect(status().isOk());
		mvc.perform(get("/responsable/empleados/nuevo")).andExpect(status().isOk());
		mvc.perform(get("/responsable/empleados/3/editar")).andExpect(status().isOk());
		mvc.perform(get("/responsable/platos")).andExpect(status().isOk());
		mvc.perform(get("/responsable/platos/nuevo")).andExpect(status().isOk());
		mvc.perform(get("/responsable/recaudacion")).andExpect(status().isOk());
		mvc.perform(get("/responsable/rentabilidad")).andExpect(status().isOk());
		mvc.perform(get("/responsable/rentabilidad-fechas")).andExpect(status().isOk());
		// el formulario de pedidos muestra la unidad del responsable y los platos de su menú, con la fecha de hoy
		mvc.perform(get("/responsable/pedidos/nuevo")).andExpect(status().isOk())
				.andExpect(content().string(Matchers.containsString("Parrilla Don Mario")))
				.andExpect(content().string(Matchers.containsString("Hamburguesa completa")))
				.andExpect(content().string(Matchers.containsString(java.time.LocalDate.now().toString())))
				.andExpect(content().string(Matchers.not(Matchers.containsString("ID de la Unidad"))));
	}

	@Test
	@WithMockUser(username = "lucia.cocina@epicentro.test", roles = "EMPLEADO")
	void vistasDelEmpleadoConHistorialDeVariosFestivales() throws Exception {
		mvc.perform(get("/empleado")).andExpect(status().isOk())
				.andExpect(content().string(Matchers.containsString("Parrilla Don Mario")))
				.andExpect(content().string(Matchers.containsString("Parrilla del Otono")));

		// Lucia (staff 3) trabajó en dos unidades: cada una tiene su recibo.
		var historial = asignacionRepository.historialDe(3L);
		org.assertj.core.api.Assertions.assertThat(historial).hasSize(2);
		for (var asignacion : historial) {
			mvc.perform(get("/empleado/recibo/" + asignacion.getId())).andExpect(status().isOk())
					.andExpect(content().contentType("application/pdf"));
		}
	}

	@Test
	@WithMockUser(username = "marcos.caja@epicentro.test", roles = "EMPLEADO")
	void elCajeroVeLaCajaDelDia() throws Exception {
		mvc.perform(get("/empleado")).andExpect(status().isOk())
				.andExpect(content().string(Matchers.containsString("Caja del día")))
				.andExpect(content().string(Matchers.containsString("Cerrar caja")));
	}

	@Test
	@WithMockUser(username = "lucia.cocina@epicentro.test", roles = "EMPLEADO")
	void unCocineroNoVeLaCaja() throws Exception {
		mvc.perform(get("/empleado")).andExpect(status().isOk())
				.andExpect(content().string(Matchers.not(Matchers.containsString("Caja del día"))));
	}

	@Test
	@WithMockUser(username = "marcos.caja@epicentro.test", roles = "EMPLEADO")
	void unEmpleadoNoPuedeDescargarElReciboDeOtro() throws Exception {
		Long deLucia = asignacionRepository.historialDe(3L).get(0).getId();
		mvc.perform(get("/empleado/recibo/" + deLucia)).andExpect(status().isNotFound());
	}
}
