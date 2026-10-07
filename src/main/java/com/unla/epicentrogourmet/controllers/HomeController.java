package com.unla.epicentrogourmet.controllers;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IFestivalService;

@Controller
public class HomeController {

	private final IFestivalService festivalService;

	public HomeController(IFestivalService festivalService) {
		this.festivalService = festivalService;
	}

	@GetMapping("/")
	public ModelAndView index() {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.HOME_INDEX);
		mAV.addObject("festivales", festivalService.getAll());
		mAV.addObject("hoy", LocalDate.now());
		return mAV;
	}
}
