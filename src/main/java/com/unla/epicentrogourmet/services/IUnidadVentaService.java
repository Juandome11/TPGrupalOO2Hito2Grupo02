package com.unla.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;

import com.unla.epicentrogourmet.dtos.RegistroUnidadResponsableDTO;
import com.unla.epicentrogourmet.entities.UnidadVenta;

public interface IUnidadVentaService {

	/** Registra la unidad con su Responsable y genera el usuario (rol RESPONSABLE) del responsable. */
	UnidadVenta registrarConResponsable(RegistroUnidadResponsableDTO dto);

	List<UnidadVenta> findByFestival(Long festivalId);

	/** Unidad que tiene asignada el responsable con ese usuario. */
	Optional<UnidadVenta> findByResponsableUsername(String username);
}
