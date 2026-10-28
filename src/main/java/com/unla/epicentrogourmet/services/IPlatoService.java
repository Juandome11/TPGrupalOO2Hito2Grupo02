package com.unla.epicentrogourmet.services;

import java.util.List;
import java.util.Map;
import com.unla.epicentrogourmet.dtos.PlatoDTO;
import com.unla.epicentrogourmet.entities.Plato;
import com.unla.epicentrogourmet.dtos.PlatoAltaDTO;

public interface IPlatoService {
    Plato alta(String usernameResponsable, PlatoAltaDTO dto);
    List<Plato> listar(String usernameResponsable);
    PlatoDTO obtenerPlatoEstrella(Long unidadId, Long festivalId);

    // Agrega esta línea:
    Map<String, Object> obtenerDatosPlatoEstrellaYRanking(Long unidadId, Long festivalId);
}