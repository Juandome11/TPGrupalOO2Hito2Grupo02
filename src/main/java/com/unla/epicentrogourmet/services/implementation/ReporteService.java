package com.unla.epicentrogourmet.services.implementation;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.ReporteVentaDTO;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.services.IReporteService;

@Service("reporteService")
public class ReporteService implements IReporteService {

	private final IPedidoRepository pedidoRepository;

	public ReporteService(IPedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public ReporteVentaDTO recaudacion(UnidadVenta unidad) {
		BigDecimal total = pedidoRepository.sumarRecaudacion(unidad.getId(), unidad.getFestival().getId());
		return new ReporteVentaDTO(unidad.getFestival().getNombre(), unidad.getNombreComercial(),
				total == null ? BigDecimal.ZERO : total);
	}
}
