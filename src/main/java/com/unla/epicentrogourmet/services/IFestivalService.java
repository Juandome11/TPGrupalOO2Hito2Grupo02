package com.unla.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;

import com.unla.epicentrogourmet.entities.Festival;

public interface IFestivalService {

	List<Festival> getAll();

	Optional<Festival> findById(Long id);
}
