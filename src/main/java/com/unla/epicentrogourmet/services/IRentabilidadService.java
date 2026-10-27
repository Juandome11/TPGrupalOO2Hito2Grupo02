package com.unla.epicentrogourmet.services;

import com.unla.epicentrogourmet.dtos.ReporteRentabilidadDTO;
import com.unla.epicentrogourmet.entities.UnidadVenta;

public interface IRentabilidadService {
    ReporteRentabilidadDTO rentabilidad(UnidadVenta unidad);
}