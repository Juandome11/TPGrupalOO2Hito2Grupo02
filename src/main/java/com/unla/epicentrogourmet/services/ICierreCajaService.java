package com.unla.epicentrogourmet.services;

import java.util.List;

import com.unla.epicentrogourmet.dtos.ResumenCajaDTO;
import com.unla.epicentrogourmet.entities.CierreCaja;
import com.unla.epicentrogourmet.entities.Staff;

public interface ICierreCajaService {

	/** Lo recaudado hoy por la unidad del cajero y si ya cerró su caja. */
	ResumenCajaDTO resumenDelDia(Staff empleado);

	/** Cierra la caja del día: registra el total recaudado. Solo cajeros, una vez por día. */
	CierreCaja cerrar(Staff empleado);

	/** Cierres anteriores del cajero, del más reciente al más antiguo. */
	List<CierreCaja> historial(Staff empleado);
}
