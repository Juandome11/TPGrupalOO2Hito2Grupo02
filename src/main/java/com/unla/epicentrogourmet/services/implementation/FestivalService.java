package com.unla.epicentrogourmet.services.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.FestivalAltaDTO;
import com.unla.epicentrogourmet.entities.Costo;

import org.springframework.stereotype.Service;

import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.repositories.IFestivalRepository;
import com.unla.epicentrogourmet.services.IFestivalService;
@Service("festivalService")
public class FestivalService implements IFestivalService {

	private final IFestivalRepository festivalRepository;

	public FestivalService(IFestivalRepository festivalRepository) {
		this.festivalRepository = festivalRepository;
	}

	@Override
	public List<Festival> getAll() {
		return festivalRepository.findAllByOrderByFechaInicioAsc();
	}

	@Override
	public Optional<Festival> findById(Long id) {
		return festivalRepository.findById(id);
	}
	@Override
    @Transactional
    public Festival crear(FestivalAltaDTO dto) {
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
	Festival festival = new Festival(dto.getNombre().trim(), dto.getTemporada().trim(),
                dto.getFechaInicio(), dto.getFechaFin());
        festival = festivalRepository.save(festival);

        Costo costo = new Costo();
        costo.setFestival(festival);
        costo.setCostoPorSuperficie(dto.getCostoPorSuperficie());
        costo.setCostoPorMontaje(dto.getCostoPorMontaje());
        costo.setPlusElectricidad(dto.getPlusElectricidad());
        costo.setSueldoBase(dto.getSueldoBase());
        costo.setMontoAnioAntiguedad(dto.getMontoAnioAntiguedad());
        costo.setPlusCocinero(dto.getPlusCocinero());
        costo.setPlusCocineroAyudante(dto.getPlusCocineroAyudante());
        costo.setPlusLavaplatos(dto.getPlusLavaplatos());
        festival.setCosto(costo);
        return festivalRepository.save(festival);
    }
}
