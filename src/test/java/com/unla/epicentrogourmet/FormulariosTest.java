package com.unla.epicentrogourmet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.repositories.IAsignacionRepository;
import com.unla.epicentrogourmet.repositories.IStaffRepository;
import com.unla.epicentrogourmet.services.IEmailService;

/**
 * Prueba los formularios (casos felices y errores de validación). Cada test se deshace al terminar
 * y el mail está simulado, así que no ensucia la base ni envía correos.
 * Solo corre si están definidas las variables de entorno de la base.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class FormulariosTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private IAsignacionRepository asignacionRepository;

	@Autowired
	private IStaffRepository staffRepository;

	@MockBean
	private IEmailService emailService;

	private MockHttpServletRequestBuilder altaEmpleado(LocalDate nacimiento, String dni) {
		return post("/responsable/empleados").with(csrf())
				.param("tipo", "COCINERO").param("rolCocina", "COCINERO_AYUDANTE")
				.param("nombre", "Nuevo").param("apellido", "Empleado").param("dni", dni)
				.param("email", "nuevo.empleado@epicentro.test")
				.param("fechaNacimiento", nacimiento.toString())
				.param("fechaIngreso", LocalDate.now().toString());
	}

	@Test
	@WithMockUser(username = "resp.parrilla", roles = "RESPONSABLE")
	void altaDeEmpleadoValidoEnviaElMailConSuUsuarioYContrasenia() throws Exception {
		when(emailService.enviarCredencialesEmpleado(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

		mvc.perform(altaEmpleado(LocalDate.now().minusYears(25), "40111222")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/responsable/empleados"));

		verify(emailService).enviarCredencialesEmpleado(org.mockito.ArgumentMatchers.eq("nuevo.empleado@epicentro.test"),
				anyString(), org.mockito.ArgumentMatchers.eq("nuevo.empleado@epicentro.test"), anyString());
	}

	@Test
	@WithMockUser(username = "resp.parrilla", roles = "RESPONSABLE")
	void altaDeEmpleadoMenorDeEdadMuestraElErrorEnElFormulario() throws Exception {
		mvc.perform(altaEmpleado(LocalDate.now().minusYears(16), "40111222")).andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("mayor de edad")));
	}

	@Test
	@WithMockUser(username = "resp.parrilla", roles = "RESPONSABLE")
	void altaDeEmpleadoConDniRepetidoMuestraElError() throws Exception {
		mvc.perform(altaEmpleado(LocalDate.now().minusYears(25), "30444555")).andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe una persona")));
	}

	@Test
	@WithMockUser(username = "resp.parrilla", roles = "RESPONSABLE")
	void empleadoDeUnFestivalAnteriorSeReasignaConservandoSuUsuario() throws Exception {
		Long tomasId = staffRepository.findByDni("37999000").orElseThrow().getId();
		long antes = asignacionRepository.historialDe(tomasId).size();

		mvc.perform(altaEmpleado(LocalDate.now().minusYears(25), "37999000")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/responsable/empleados"));

		// no se le crea otro usuario ni se envía mail: conserva el que ya tenía
		verifyNoInteractions(emailService);
		org.assertj.core.api.Assertions.assertThat(asignacionRepository.historialDe(tomasId)).hasSize((int) antes + 1);
		org.assertj.core.api.Assertions.assertThat(staffRepository.findByDni("37999000").orElseThrow().getUnidadVenta().getNombreComercial())
				.isEqualTo("Parrilla Don Mario");
	}

	@Test
	@WithMockUser(username = "resp.parrilla", roles = "RESPONSABLE")
	void altaDePlatoValidoYConCostoMayorAlPrecio() throws Exception {
		mvc.perform(post("/responsable/platos").with(csrf()).param("nombre", "Choripan")
				.param("precioVenta", "3000").param("costoProduccion", "1000"))
				.andExpect(status().is3xxRedirection());

		mvc.perform(post("/responsable/platos").with(csrf()).param("nombre", "Plato caro de hacer")
				.param("precioVenta", "1000").param("costoProduccion", "3000")).andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("no puede superar")));
	}

	@Test
	@WithMockUser(username = "marcos.caja@epicentro.test", roles = "EMPLEADO")
	void elCajeroCierraLaCajaUnaSolaVezPorDia() throws Exception {
		mvc.perform(post("/empleado/caja/cerrar").with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/empleado")).andExpect(flash().attributeExists("ok"));

		mvc.perform(post("/empleado/caja/cerrar").with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(flash().attribute("error", "Ya cerraste la caja de hoy."));
	}

	@Test
	@WithMockUser(username = "lucia.cocina@epicentro.test", roles = "EMPLEADO")
	void unCocineroNoPuedeCerrarCaja() throws Exception {
		mvc.perform(post("/empleado/caja/cerrar").with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(flash().attribute("error", "Solo los cajeros pueden cerrar la caja."));
	}

	private MockHttpServletRequestBuilder registro(String codigo) {
		return post("/unidad/registro").with(csrf())
				.param("festivalId", "2").param("tipo", "FOOD_TRUCK").param("nombreComercial", "Tacos del Norte")
				.param("superficie", "10").param("codigo", codigo).param("patente", "XY987ZT")
				.param("nombre", "Rosa").param("apellido", "Vega").param("dni", "27123456")
				.param("email", "rosa.vega@epicentro.test").param("fechaNacimiento", "1985-03-20")
				.param("username", "resp.tacos").param("password", "secreto1");
	}

	@Test
	void registroDeUnidadConResponsableCreaElUsuario() throws Exception {
		mvc.perform(registro("FT-7654321")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?registrado=true"));
	}

	@Test
	void registroConCodigoDeFormatoInvalidoMuestraElError() throws Exception {
		mvc.perform(registro("PD-7654321")).andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("formato FT-")));
	}
}
