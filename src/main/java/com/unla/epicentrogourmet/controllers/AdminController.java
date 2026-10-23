package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IFestivalService;
import com.unla.epicentrogourmet.services.IUnidadVentaService;
import com.unla.epicentrogourmet.services.IReporteService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final IFestivalService festivalService;
	private final IUnidadVentaService unidadVentaService;
	private final IReporteService reporteService;

	public AdminController(IFestivalService festivalService, IUnidadVentaService unidadVentaService, IReporteService reporteService) {
		this.festivalService = festivalService;
		this.unidadVentaService = unidadVentaService;
		this.reporteService = reporteService;
	}

	@GetMapping("")
	public ModelAndView festivales() {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.ADMIN_FESTIVALES);
		mAV.addObject("festivales", festivalService.getAll());
		return mAV;
	}

	@GetMapping("/festival/{id}/unidades")
	public ModelAndView unidades(@PathVariable("id") Long id) {
		Festival festival = festivalService.findById(id).orElse(null);
		if (festival == null) {
			return new ModelAndView(new RedirectView(ViewRouteHelper.ADMIN_ROOT));
		}
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.ADMIN_UNIDADES);
		mAV.addObject("festival", festival);
		mAV.addObject("unidades", unidadVentaService.findByFestival(id));
		return mAV;
	}

	@GetMapping("/festival/{id}/ranking")
	public ModelAndView ranking(@PathVariable("id") Long id) {
		Festival festival = festivalService.findById(id).orElse(null);
		if (festival == null) {
			return new ModelAndView(new RedirectView(ViewRouteHelper.ADMIN_ROOT));
		}
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.ADMIN_RANKING);
		mAV.addObject("festival", festival);
		mAV.addObject("ranking", reporteService.rankingRecaudacion(id));
		return mAV;
	}

	
}
