package com.unla.epicentrogourmet.services.implementation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.PlatoAltaDTO;
import com.unla.epicentrogourmet.dtos.PlatoDTO;
import com.unla.epicentrogourmet.dtos.PlatoRankingDTO;
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
        String nombre = dto.getNombre().trim();
        if (platoRepository.existsByUnidadVentaIdAndNombreIgnoreCase(unidad.getId(), nombre)) {
            throw new NegocioException("Tu unidad ya tiene un plato llamado \"" + nombre + "\".");
        }
        Plato plato = new Plato();
        plato.setNombre(nombre);
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

    @Override
    @Transactional(readOnly = true)
    public PlatoDTO obtenerPlatoEstrella(Long unidadId, Long festivalId) {
        List<Plato> platos = platoRepository.findPlatoEstrella(unidadId, festivalId);

        Plato plato;
        if (platos.isEmpty()) {
            List<Plato> todosLosPlatos = platoRepository.findByUnidadVentaIdOrderByNombre(unidadId);
            if (todosLosPlatos.isEmpty()) {
                throw new NegocioException("No se encontró un plato estrella para los filtros dados.");
            }
            plato = todosLosPlatos.get(0);
        } else {
            plato = platos.get(0);
        }

        return new PlatoDTO(
                plato.getId(),
                plato.getNombre(),
                plato.getPrecioVenta(),
                plato.getCostoProduccion(),
                plato.getUnidadVenta() != null ? plato.getUnidadVenta().getId() : null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerDatosPlatoEstrellaYRanking(Long unidadId, Long festivalId) {
        List<PlatoRankingDTO> ranking = platoRepository.obtenerRankingPlatos(unidadId, festivalId);

        if (ranking.isEmpty()) {
            throw new NegocioException("No hay ventas registradas para esta unidad en este festival.");
        }

        PlatoRankingDTO platoEstrella = ranking.get(0); // El 1° puesto

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("platoEstrella", platoEstrella);
        resultado.put("ranking", ranking);
        return resultado;
    }

    private UnidadVenta unidadDe(String usernameResponsable) {
        return unidadVentaRepository.findByResponsableUsername(usernameResponsable)
                .orElseThrow(() -> new NegocioException("No tenés una unidad de venta asignada."));
    }
}