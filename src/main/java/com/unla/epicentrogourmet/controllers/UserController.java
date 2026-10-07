package com.unla.epicentrogourmet.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.unla.epicentrogourmet.entities.UserRole;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;

@Controller
public class UserController {

	@GetMapping("/login")
	public String login() {
		return ViewRouteHelper.USER_LOGIN;
	}

	/** Después de iniciar sesión, cada rol va a su pantalla. */
	@GetMapping("/loginsuccess")
	public ModelAndView loginSuccess(Authentication authentication) {
		boolean admin = tieneRol(authentication, UserRole.ADMIN);
		boolean responsable = tieneRol(authentication, UserRole.RESPONSABLE);
		String destino = admin ? ViewRouteHelper.ADMIN_ROOT
				: responsable ? ViewRouteHelper.RESP_ROOT : ViewRouteHelper.EMP_ROOT;
		return new ModelAndView(new RedirectView(destino));
	}

	private boolean tieneRol(Authentication authentication, String rol) {
		return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rol));
	}
}
