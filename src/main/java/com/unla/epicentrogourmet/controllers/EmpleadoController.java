package com.unla.epicentrogourmet.controllers;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.dtos.ReciboSueldoDTO;
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

	/** Mi información: festival y unidad en la que trabajo, si el festival está activo hoy y mi sueldo. */
	@GetMapping("")
	public ModelAndView index(Principal principal) {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMP_HOME);
		Staff empleado = staffService.findByUsername(principal.getName()).orElse(null);
		mAV.addObject("empleado", empleado);
		if (empleado != null && empleado.getUnidadVenta() != null) {
			Festival festival = empleado.getUnidadVenta().getFestival();
			mAV.addObject("festival", festival);
			mAV.addObject("unidad", empleado.getUnidadVenta());
			mAV.addObject("festivalActivo", festival.estaActivo(LocalDate.now()));
			if (festival.getCosto() != null) {
				mAV.addObject("recibo", liquidacionService.calcular(empleado, festival));
			}
		}
		return mAV;
	}

	@GetMapping("/recibo/{festivalId}")
	public ResponseEntity<byte[]> recibo(Principal principal, @PathVariable("festivalId") Long festivalId) {
		Staff empleado = staffService.findByUsername(principal.getName()).orElse(null);
		if (empleado == null || empleado.getUnidadVenta() == null
				|| !empleado.getUnidadVenta().getFestival().getId().equals(festivalId)) {
			return ResponseEntity.notFound().build();
		}
		ReciboSueldoDTO recibo = liquidacionService.calcular(empleado, empleado.getUnidadVenta().getFestival());
		byte[] pdf = reciboPdfService.generar(recibo);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"recibo-sueldo-" + festivalId + ".pdf\"")
				.contentType(MediaType.APPLICATION_PDF).body(pdf);
	}
}
