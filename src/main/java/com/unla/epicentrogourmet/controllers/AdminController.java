package com.unla.epicentrogourmet.controllers;
import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import com.unla.epicentrogourmet.dtos.FestivalAltaDTO;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IFestivalService;
import com.unla.epicentrogourmet.services.IUnidadVentaService;

import jakarta.validation.Valid;

import com.unla.epicentrogourmet.services.IReporteService;
import com.unla.epicentrogourmet.services.IPlatoService; // <-- Importante

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final IFestivalService festivalService;
    private final IUnidadVentaService unidadVentaService;
    private final IReporteService reporteService;
    private final IPlatoService platoService; // <-- Inyectamos el servicio de platos

    public AdminController(IFestivalService festivalService,
                           IUnidadVentaService unidadVentaService,
                           IReporteService reporteService,
                           IPlatoService platoService) { // <-- Actualizamos el constructor
        this.festivalService = festivalService;
        this.unidadVentaService = unidadVentaService;
        this.reporteService = reporteService;
        this.platoService = platoService;
    }

    @GetMapping("")
    public ModelAndView festivales() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.ADMIN_FESTIVALES);
        mAV.addObject("festivales", festivalService.getAll());
        return mAV;
    }

	@GetMapping("")
	public ModelAndView festivales() {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.ADMIN_FESTIVALES);
		mAV.addObject("festivales", festivalService.getAll());
		return mAV;
	}
	@GetMapping("/festival/nuevo")
    public ModelAndView nuevoFestival() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.ADMIN_FESTIVAL_FORM);
        FestivalAltaDTO dto = new FestivalAltaDTO();
        dto.setCostoPorSuperficie(new java.math.BigDecimal("500"));
        dto.setCostoPorMontaje(new java.math.BigDecimal("10"));
        dto.setPlusElectricidad(new java.math.BigDecimal("2000"));
        dto.setSueldoBase(new java.math.BigDecimal("100000"));
        dto.setMontoAnioAntiguedad(new java.math.BigDecimal("5000"));
        dto.setPlusCocinero(new java.math.BigDecimal("150000"));
        dto.setPlusCocineroAyudante(new java.math.BigDecimal("100000"));
        dto.setPlusLavaplatos(new java.math.BigDecimal("50000"));
        mAV.addObject("festivalAlta", dto);
        return mAV;
    }

    @PostMapping("/festival/nuevo")
    public ModelAndView crearFestival(@Valid @ModelAttribute("festivalAlta") FestivalAltaDTO dto,
            BindingResult bindingResult, RedirectAttributes flash) {
        if (dto.getFechaInicio() != null && dto.getFechaFin() != null
                && dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            bindingResult.rejectValue("fechaFin", "fechaFin.invalida",
                    "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        if (bindingResult.hasErrors()) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.ADMIN_FESTIVAL_FORM);
            mAV.addObject("festivalAlta", dto);
            return mAV;
        }
        festivalService.crear(dto);
        flash.addFlashAttribute("ok", "El festival se creó correctamente.");
        return new ModelAndView(new RedirectView(ViewRouteHelper.ADMIN_ROOT));
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

    @GetMapping("/festival/{id}/mayores-canon")
    public ModelAndView mayoresCanon(@PathVariable("id") Long id,
                                     @RequestParam(name = "x", defaultValue = "5") int cantidad) {
        Festival festival = festivalService.findById(id).orElse(null);
        if (festival == null) {
            return new ModelAndView(new RedirectView(ViewRouteHelper.ADMIN_ROOT));
        }
        if (cantidad < 1) cantidad = 1;
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.ADMIN_MAYORES_CANON);
        mAV.addObject("festival", festival);
        mAV.addObject("cantidad", cantidad);
        mAV.addObject("reporte", reporteService.mayoresCanon(id, cantidad));
        return mAV;
    }


    @GetMapping("/festival/{festivalId}/unidad/{unidadId}/plato-estrella")
    public String platoEstrella(@PathVariable Long festivalId, @PathVariable Long unidadId, Model model) {
        try {
            Map<String, Object> datos = platoService.obtenerDatosPlatoEstrellaYRanking(unidadId, festivalId);
            model.addAttribute("platoEstrella", datos.get("platoEstrella"));
            model.addAttribute("ranking", datos.get("ranking"));
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("festivalId", festivalId);
        return "admin/plato-estrella";
    }
}