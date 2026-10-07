package com.unla.epicentrogourmet.services;

import com.unla.epicentrogourmet.dtos.ReciboSueldoDTO;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.Staff;

public interface ILiquidacionService {

	/** Calcula lo que cobra el empleado por su trabajo en el festival, según los Costo del festival. */
	ReciboSueldoDTO calcular(Staff empleado, Festival festival);
}
