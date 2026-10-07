package com.unla.epicentrogourmet.controllers;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.unla.epicentrogourmet.dtos.RegistroUnidadResponsableDTO;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IFestivalService;
import com.unla.epicentrogourmet.services.IUnidadVentaService;

import jakarta.validation.Valid;

/** Registro público: una Unidad de Venta con su Responsable (se genera su usuario). */
@Controller
@RequestMapping("/unidad")
public class UnidadVentaController {

	private static final String DTO_ATTR = "registro";

	private final IUnidadVentaService unidadVentaService;
	private final IFestivalService festivalService;

	public UnidadVentaController(IUnidadVentaService unidadVentaService, IFestivalService festivalService) {
		this.unidadVentaService = unidadVentaService;
		this.festivalService = festivalService;
	}

	@GetMapping("/registro")
	public ModelAndView registro(@RequestParam(name = "festivalId", required = false) Long festivalId) {
		RegistroUnidadResponsableDTO dto = new RegistroUnidadResponsableDTO();
		dto.setFestivalId(festivalId);
		dto.setTipo(RegistroUnidadResponsableDTO.FOOD_TRUCK);
		return formulario(dto);
	}

	@PostMapping("/registro")
	public ModelAndView registrar(@Valid @ModelAttribute(DTO_ATTR) RegistroUnidadResponsableDTO dto,
			BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			try {
				unidadVentaService.registrarConResponsable(dto);
				return new ModelAndView(new RedirectView("/login?registrado=true"));
			} catch (NegocioException e) {
				bindingResult.reject("negocio", e.getMessage());
			}
		}
		return formulario(dto);
	}

	private ModelAndView formulario(RegistroUnidadResponsableDTO dto) {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.UNIDAD_REGISTRO);
		mAV.addObject(DTO_ATTR, dto);
		mAV.addObject("festivales", festivalService.getAll().stream()
				.filter(f -> !f.getFechaFin().isBefore(LocalDate.now())).toList());
		return mAV;
	}
}
