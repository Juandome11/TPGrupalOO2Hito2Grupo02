package com.unla.epicentrogourmet.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.dtos.ReciboSueldoDTO;
import com.unla.epicentrogourmet.dtos.TrabajoRealizadoDTO;
import com.unla.epicentrogourmet.entities.Asignacion;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.Staff;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.ILiquidacionService;
import com.unla.epicentrogourmet.services.IReciboPdfService;
import com.unla.epicentrogourmet.services.IStaffService;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

	private final IStaffService staffService;
	private final ILiquidacionService liquidacionService;
	private final IReciboPdfService reciboPdfService;

	public EmpleadoController(IStaffService staffService, ILiquidacionService liquidacionService,
			IReciboPdfService reciboPdfService) {
		this.staffService = staffService;
		this.liquidacionService = liquidacionService;
		this.reciboPdfService = reciboPdfService;
	}

	/**
	 * Mi información: dónde trabajo hoy (y si el festival está en curso) y el historial de festivales y
	 * unidades en las que trabajé, con el sueldo cobrado en cada uno.
	 */
	@GetMapping("")
	public ModelAndView index(Principal principal) {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMP_HOME);
		Staff empleado = staffService.findByUsername(principal.getName()).orElse(null);
		mAV.addObject("empleado", empleado);
		if (empleado == null) {
			return mAV;
		}
		if (empleado.getUnidadVenta() != null) {
			Festival festival = empleado.getUnidadVenta().getFestival();
			mAV.addObject("festival", festival);
			mAV.addObject("unidad", empleado.getUnidadVenta());
			mAV.addObject("festivalActivo", festival.estaActivo(LocalDate.now()));
		}
		mAV.addObject("historial", armarHistorial(empleado));
		return mAV;
	}

	@GetMapping("/recibo/{asignacionId}")
	public ResponseEntity<byte[]> recibo(Principal principal, @PathVariable("asignacionId") Long asignacionId) {
		Staff empleado = staffService.findByUsername(principal.getName()).orElse(null);
		Asignacion asignacion = empleado == null ? null
				: staffService.findAsignacion(asignacionId, empleado).orElse(null);
		if (asignacion == null) {
			return ResponseEntity.notFound().build();
		}
		ReciboSueldoDTO recibo = liquidacionService.calcular(empleado, asignacion.getUnidadVenta());
		byte[] pdf = reciboPdfService.generar(recibo);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"recibo-sueldo-" + asignacionId + ".pdf\"")
				.contentType(MediaType.APPLICATION_PDF).body(pdf);
	}

	private List<TrabajoRealizadoDTO> armarHistorial(Staff empleado) {
		LocalDate hoy = LocalDate.now();
		return staffService.historialDe(empleado).stream().map(a -> {
			Festival festival = a.getFestival();
			ReciboSueldoDTO recibo = festival.getCosto() == null ? null
					: liquidacionService.calcular(empleado, a.getUnidadVenta());
			boolean actual = empleado.getUnidadVenta() != null
					&& empleado.getUnidadVenta().getId().equals(a.getUnidadVenta().getId());
			return new TrabajoRealizadoDTO(a.getId(), festival.estaActivo(hoy), actual, recibo, festival.getNombre(),
					a.getUnidadVenta().getNombreComercial());
		}).toList();
	}
}
