package com.unla.epicentrogourmet.services;

import com.unla.epicentrogourmet.dtos.ReciboSueldoDTO;
import com.unla.epicentrogourmet.entities.Staff;
import com.unla.epicentrogourmet.entities.UnidadVenta;

public interface ILiquidacionService {

	/** Calcula lo que cobró el empleado por trabajar en esa unidad, según los Costo del festival de la unidad. */
	ReciboSueldoDTO calcular(Staff empleado, UnidadVenta unidad);
}
