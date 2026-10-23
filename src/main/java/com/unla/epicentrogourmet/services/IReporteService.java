package com.unla.epicentrogourmet.services;
import java.util.List;
import com.unla.epicentrogourmet.dtos.ReporteVentaDTO;
import com.unla.epicentrogourmet.entities.UnidadVenta;

public interface IReporteService {

	/** Recaudación total de la unidad en el festival en el que participa. */
	ReporteVentaDTO recaudacion(UnidadVenta unidad);

	/** Unidades de un festival ordenadas por recaudación descendente. */
	List<ReporteVentaDTO> rankingRecaudacion(Long festivalId);
}
