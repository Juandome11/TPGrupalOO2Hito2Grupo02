package com.unla.epicentrogourmet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
	}

	@Test
	@WithMockUser(username = "resp.parrilla", roles = "RESPONSABLE")
	void vistasDelResponsable() throws Exception {
		mvc.perform(get("/responsable")).andExpect(status().isOk());
		mvc.perform(get("/responsable/empleados")).andExpect(status().isOk());
		mvc.perform(get("/responsable/empleados/nuevo")).andExpect(status().isOk());
		mvc.perform(get("/responsable/empleados/3/editar")).andExpect(status().isOk());
		mvc.perform(get("/responsable/platos")).andExpect(status().isOk());
		mvc.perform(get("/responsable/platos/nuevo")).andExpect(status().isOk());
		mvc.perform(get("/responsable/recaudacion")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "lucia.cocina@epicentro.test", roles = "EMPLEADO")
	void vistasDelEmpleado() throws Exception {
		mvc.perform(get("/empleado")).andExpect(status().isOk());
		mvc.perform(get("/empleado/recibo/1")).andExpect(status().isOk())
				.andExpect(content().contentType("application/pdf"));
	}
}
