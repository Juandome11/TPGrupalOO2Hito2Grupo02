package com.unla.epicentrogourmet.services.implementation;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Comparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.ReporteVentaDTO;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.services.IReporteService;

@Service("reporteService")
public class ReporteService implements IReporteService {

	private final IPedidoRepository pedidoRepository;
	private final IUnidadVentaRepository unidadVentaRepository;


	public ReporteService(IPedidoRepository pedidoRepository, IUnidadVentaRepository unidadVentaRepository) {
		this.pedidoRepository = pedidoRepository;
		this.unidadVentaRepository = unidadVentaRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public ReporteVentaDTO recaudacion(UnidadVenta unidad) {
		BigDecimal total = pedidoRepository.sumarRecaudacion(unidad.getId(), unidad.getFestival().getId());
		return new ReporteVentaDTO(unidad.getFestival().getNombre(), unidad.getNombreComercial(),
				total == null ? BigDecimal.ZERO : total);
	}
	@Override
	@Transactional(readOnly = true)
	public List<ReporteVentaDTO> rankingRecaudacion(Long festivalId) {
		return unidadVentaRepository.findByFestivalId(festivalId).stream()
				.map(this::recaudacion)
				.sorted(Comparator.comparing(ReporteVentaDTO::getRecaudacionTotal).reversed())
				.collect(Collectors.toList());
	}

}
