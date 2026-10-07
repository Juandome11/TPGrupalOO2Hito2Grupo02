package com.unla.epicentrogourmet.services;

import com.unla.epicentrogourmet.dtos.ReciboSueldoDTO;

public interface IReciboPdfService {

	byte[] generar(ReciboSueldoDTO recibo);
}
