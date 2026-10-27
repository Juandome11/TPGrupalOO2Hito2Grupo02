package com.unla.epicentrogourmet.services;

import java.time.LocalDate;

import com.unla.epicentrogourmet.dtos.ReporteRentabilidadDTO;
import com.unla.epicentrogourmet.entities.UnidadVenta;

public interface IRentabilidadService {
    ReporteRentabilidadDTO rentabilidad(UnidadVenta unidad);

    ReporteRentabilidadDTO rentabilidad(UnidadVenta unidad, LocalDate desde, LocalDate hasta);
}