package com.unla.epicentrogourmet.services;

import java.util.List;

import com.unla.epicentrogourmet.dtos.PlatoAltaDTO;
import com.unla.epicentrogourmet.entities.Plato;

public interface IPlatoService {

	Plato alta(String usernameResponsable, PlatoAltaDTO dto);

	List<Plato> listar(String usernameResponsable);
}
