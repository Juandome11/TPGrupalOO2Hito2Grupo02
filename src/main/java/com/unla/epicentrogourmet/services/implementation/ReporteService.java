package com.unla.epicentrogourmet.services.implementation;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.ReporteVentaDTO;
import com.unla.epicentrogourmet.dtos.ReporteMayoresCanonDTO;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.repositories.IFestivalRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.services.IReporteService;

@Service("reporteService")
public class ReporteService implements IReporteService {

	private final IPedidoRepository pedidoRepository;
	private final IFestivalRepository festivalRepository;
	private final IUnidadVentaRepository unidadVentaRepository;

	public ReporteService(IPedidoRepository pedidoRepository, IFestivalRepository festivalRepository, IUnidadVentaRepository unidadVentaRepository) {
		this.pedidoRepository = pedidoRepository;
		this.festivalRepository = festivalRepository;
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

	@Override
	@Transactional(readOnly = true)
	public List<ReporteMayoresCanonDTO> mayoresCanon(Long festivalId, int cantidad) {
		if (cantidad < 1) {
			throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
		}
		Festival festival = festivalRepository.findById(festivalId)
				.orElseThrow(() -> new IllegalArgumentException("El festival no existe."));
		if (festival.getCosto() == null) {
			return List.of();
		}
		return unidadVentaRepository.findByFestivalId(festivalId).stream()
				.map(u -> new ReporteMayoresCanonDTO(u.getNombreComercial(), u.getCodigo(), u.getTipo(), u.calcularCanon(festival.getCosto())))
				.sorted(Comparator.comparing(ReporteMayoresCanonDTO::getCanon).reversed())
				.limit(cantidad)
				.collect(Collectors.toList());
	}

}
