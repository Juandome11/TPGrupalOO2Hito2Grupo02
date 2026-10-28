package com.unla.epicentrogourmet.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.CierreCaja;

@Repository("cierreCajaRepository")
public interface ICierreCajaRepository extends JpaRepository<CierreCaja, Long> {

	Optional<CierreCaja> findByCajeroIdAndFecha(Long cajeroId, LocalDate fecha);

	/** Cierres del cajero, del más reciente al más antiguo. */
	List<CierreCaja> findByCajeroIdOrderByFechaDesc(Long cajeroId);
}
