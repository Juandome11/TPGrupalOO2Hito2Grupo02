package com.unla.epicentrogourmet.controllers;

import java.time.LocalDate;
import java.security.Principal;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.unla.epicentrogourmet.dtos.EmpleadoDTO;
import com.unla.epicentrogourmet.dtos.PlatoAltaDTO;
import com.unla.epicentrogourmet.entities.RolCocina;
import com.unla.epicentrogourmet.entities.Turno;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IPlatoService;
import com.unla.epicentrogourmet.services.IReporteService;
import com.unla.epicentrogourmet.services.IStaffService;
import com.unla.epicentrogourmet.services.IUnidadVentaService;
import com.unla.epicentrogourmet.services.IRentabilidadService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/responsable")
@PreAuthorize("hasRole('ROLE_RESPONSABLE')")
public class ResponsableController {

	private static final String EMPLEADO_ATTR = "empleado";
	private static final String PLATO_ATTR = "plato";

	private final IUnidadVentaService unidadVentaService;
	private final IStaffService staffService;
	private final IPlatoService platoService;
	private final IReporteService reporteService;
	private final IRentabilidadService rentabilidadService;

	public ResponsableController(IUnidadVentaService unidadVentaService, IStaffService staffService,
			IPlatoService platoService, IReporteService reporteService, IRentabilidadService rentabilidadService) {
		this.unidadVentaService = unidadVentaService;
		this.staffService = staffService;
		this.platoService = platoService;
		this.reporteService = reporteService;
		this.rentabilidadService = rentabilidadService;
	}

	// ---------------------------------------------------------------- inicio

	@GetMapping("")
	public ModelAndView index(Principal principal) {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESP_HOME);
		Optional<UnidadVenta> unidad = unidadVentaService.findByResponsableUsername(principal.getName());
		mAV.addObject("unidad", unidad.orElse(null));
		unidad.ifPresent(u -> {
			if (u.getFestival().getCosto() != null) {
				mAV.addObject("canon", u.calcularCanon(u.getFestival().getCosto()));
			}
		});
		return mAV;
	}

	@GetMapping("/index")
	public ModelAndView indexAlternativo(Principal principal) {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESP_HOME);
		Optional<UnidadVenta> unidad = unidadVentaService.findByResponsableUsername(principal.getName());
		mAV.addObject("unidad", unidad.orElse(null));
		return mAV;
	}

	// ------------------------------------------------------------- empleados

	@GetMapping("/empleados")
	public ModelAndView empleados(Principal principal,
			@RequestParam(name = "texto", required = false) String texto,
			@RequestParam(name = "dni", required = false) String dni,
			@RequestParam(name = "tipo", required = false) String tipo) {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESP_EMPLEADOS);
		mAV.addObject("empleados", staffService.buscarActivos(principal.getName(), texto, dni, tipo));
		mAV.addObject("eliminados", staffService.listarEliminados(principal.getName()));
		mAV.addObject("texto", texto);
		mAV.addObject("dni", dni);
		mAV.addObject("tipo", tipo);
		return mAV;
	}

	@GetMapping("/empleados/nuevo")
	public ModelAndView nuevoEmpleado() {
		EmpleadoDTO dto = new EmpleadoDTO();
		dto.setTipo(EmpleadoDTO.COCINERO);
		return formularioEmpleado(dto, false);
	}

	@PostMapping("/empleados")
	public ModelAndView crearEmpleado(Principal principal,
			@Valid @ModelAttribute(EMPLEADO_ATTR) EmpleadoDTO dto, BindingResult bindingResult,
			RedirectAttributes flash) {
		if (!bindingResult.hasErrors()) {
			try {
				var resultado = staffService.altaEmpleado(principal.getName(), dto);
				if (resultado.reasignado()) {
					flash.addFlashAttribute("ok", "Ese empleado ya había trabajado en un festival anterior: se lo asignó a "
							+ "tu unidad y conserva su usuario y contraseña.");
				} else if (resultado.mailEnviado()) {
					flash.addFlashAttribute("ok", "Empleado dado de alta. Se le envió un mail con su usuario y contraseña.");
				} else {
					flash.addFlashAttribute("advertencia", "El empleado se dio de alta, pero no se pudo enviar el mail con "
							+ "sus credenciales. Revisá la configuración de mail.");
				}
				return new ModelAndView(new RedirectView(ViewRouteHelper.RESP_EMPLEADOS_ROOT));
			} catch (NegocioException e) {
				bindingResult.reject("negocio", e.getMessage());
			}
		}
		return formularioEmpleado(dto, false);
	}

	@GetMapping("/empleados/{id}/editar")
	public ModelAndView editarEmpleado(Principal principal, @PathVariable("id") Long id, RedirectAttributes flash) {
		try {
			return formularioEmpleado(staffService.obtenerParaEdicion(principal.getName(), id), true);
		} catch (NegocioException e) {
			flash.addFlashAttribute("error", e.getMessage());
			return new ModelAndView(new RedirectView(ViewRouteHelper.RESP_EMPLEADOS_ROOT));
		}
	}

	@PostMapping("/empleados/{id}")
	public ModelAndView modificarEmpleado(Principal principal, @PathVariable("id") Long id,
			@Valid @ModelAttribute(EMPLEADO_ATTR) EmpleadoDTO dto, BindingResult bindingResult,
			RedirectAttributes flash) {
		dto.setId(id);
		if (!bindingResult.hasErrors()) {
			try {
				staffService.modificarEmpleado(principal.getName(), dto);
				flash.addFlashAttribute("ok", "Empleado modificado.");
				return new ModelAndView(new RedirectView(ViewRouteHelper.RESP_EMPLEADOS_ROOT));
			} catch (NegocioException e) {
				bindingResult.reject("negocio", e.getMessage());
			}
		}
		return formularioEmpleado(dto, true);
	}

	@PostMapping("/empleados/{id}/eliminar")
	public RedirectView eliminarEmpleado(Principal principal, @PathVariable("id") Long id, RedirectAttributes flash) {
		try {
			staffService.bajaEmpleado(principal.getName(), id);
			flash.addFlashAttribute("ok", "Empleado dado de baja.");
		} catch (NegocioException e) {
			flash.addFlashAttribute("error", e.getMessage());
		}
		return new RedirectView(ViewRouteHelper.RESP_EMPLEADOS_ROOT);
	}

	@PostMapping("/empleados/{id}/reactivar")
	public RedirectView reactivarEmpleado(Principal principal, @PathVariable("id") Long id, RedirectAttributes flash) {
		try {
			staffService.reactivarEmpleado(principal.getName(), id);
			flash.addFlashAttribute("ok", "Empleado reactivado.");
		} catch (NegocioException e) {
			flash.addFlashAttribute("error", e.getMessage());
		}
		return new RedirectView(ViewRouteHelper.RESP_EMPLEADOS_ROOT);
	}

	private ModelAndView formularioEmpleado(EmpleadoDTO dto, boolean edicion) {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESP_EMPLEADO_FORM);
		mAV.addObject(EMPLEADO_ATTR, dto);
		mAV.addObject("edicion", edicion);
		mAV.addObject("roles", RolCocina.values());
		mAV.addObject("turnos", Turno.values());
		return mAV;
	}

	// ---------------------------------------------------------------- platos

	@GetMapping("/platos")
	public ModelAndView platos(Principal principal) {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESP_PLATOS);
		mAV.addObject("platos", platoService.listar(principal.getName()));
		return mAV;
	}

	@GetMapping("/platos/nuevo")
	public ModelAndView nuevoPlato() {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESP_PLATO_FORM);
		mAV.addObject(PLATO_ATTR, new PlatoAltaDTO());
		return mAV;
	}

	@PostMapping("/platos")
	public ModelAndView crearPlato(Principal principal, @Valid @ModelAttribute(PLATO_ATTR) PlatoAltaDTO dto,
			BindingResult bindingResult, RedirectAttributes flash) {
		if (!bindingResult.hasErrors()) {
			try {
				platoService.alta(principal.getName(), dto);
				flash.addFlashAttribute("ok", "Plato agregado al menú.");
				return new ModelAndView(new RedirectView(ViewRouteHelper.RESP_PLATOS_ROOT));
			} catch (NegocioException e) {
				bindingResult.reject("negocio", e.getMessage());
			}
		}
		return new ModelAndView(ViewRouteHelper.RESP_PLATO_FORM);
	}

	// -------------------------------------------------------------- reportes

	@GetMapping("/recaudacion")
	public ModelAndView recaudacion(Principal principal) {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESP_RECAUDACION);
		unidadVentaService.findByResponsableUsername(principal.getName())
				.ifPresent(u -> mAV.addObject("reporte", reporteService.recaudacion(u)));
		return mAV;
	}

	@GetMapping("/rentabilidad")
	public ModelAndView rentabilidad(Principal principal) {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESP_RENTABILIDAD);
		Optional<UnidadVenta> unidad = unidadVentaService.findByResponsableUsername(principal.getName());
		mAV.addObject("unidad", unidad.orElse(null));
		try {
			unidad.ifPresent(u -> mAV.addObject("reporte", rentabilidadService.rentabilidad(u)));
		} catch (NegocioException e) {
			mAV.addObject("error", e.getMessage());
		}
		return mAV;
	}

	@GetMapping("/rentabilidad-fechas")
	public ModelAndView rentabilidadFechas(
			@RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
			Principal principal) {

		ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESP_RENTABILIDAD_FECHAS);
		Optional<UnidadVenta> unidad = unidadVentaService.findByResponsableUsername(principal.getName());
		mAV.addObject("unidad", unidad.orElse(null));

		if (desde != null && hasta != null && unidad.isPresent()) {
			try {
				var reporte = rentabilidadService.rentabilidad(unidad.get(), desde, hasta);
				mAV.addObject("reporte", reporte);
				mAV.addObject("desde", desde);
				mAV.addObject("hasta", hasta);
			} catch (NegocioException e) {
				mAV.addObject("error", e.getMessage());
			}
		}
		return mAV;
	}
}
