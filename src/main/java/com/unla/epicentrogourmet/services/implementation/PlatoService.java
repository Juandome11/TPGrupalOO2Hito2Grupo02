package com.unla.epicentrogourmet.services.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.PlatoAltaDTO;
import com.unla.epicentrogourmet.entities.Plato;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.repositories.IPlatoRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.services.IPlatoService;

@Service("platoService")
public class PlatoService implements IPlatoService {

	private final IPlatoRepository platoRepository;
	private final IUnidadVentaRepository unidadVentaRepository;

	public PlatoService(IPlatoRepository platoRepository, IUnidadVentaRepository unidadVentaRepository) {
		this.platoRepository = platoRepository;
		this.unidadVentaRepository = unidadVentaRepository;
	}

	@Override
	@Transactional
	public Plato alta(String usernameResponsable, PlatoAltaDTO dto) {
		UnidadVenta unidad = unidadDe(usernameResponsable);
		if (dto.getCostoProduccion().compareTo(dto.getPrecioVenta()) > 0) {
			throw new NegocioException("El costo de producción no puede superar el precio de venta.");
		}
		Plato plato = new Plato();
		plato.setNombre(dto.getNombre());
		plato.setPrecioVenta(dto.getPrecioVenta());
		plato.setCostoProduccion(dto.getCostoProduccion());
		plato.setUnidadVenta(unidad);
		return platoRepository.save(plato);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Plato> listar(String usernameResponsable) {
		return platoRepository.findByUnidadVentaIdOrderByNombre(unidadDe(usernameResponsable).getId());
	}

	private UnidadVenta unidadDe(String usernameResponsable) {
		return unidadVentaRepository.findByResponsableUsername(usernameResponsable)
				.orElseThrow(() -> new NegocioException("No tenés una unidad de venta asignada."));
	}
}
