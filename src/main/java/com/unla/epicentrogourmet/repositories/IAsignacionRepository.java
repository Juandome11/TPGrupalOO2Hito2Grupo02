package com.unla.epicentrogourmet.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.unla.epicentrogourmet.entities.Staff;

import com.unla.epicentrogourmet.entities.Asignacion;

@Repository("asignacionRepository")
public interface IAsignacionRepository extends JpaRepository<Asignacion, Long> {

	/** Todo lo que trabajó el empleado, del festival más reciente al más antiguo. */
	@Query("SELECT a FROM Asignacion a WHERE a.staff.id = :staffId ORDER BY a.unidadVenta.festival.fechaInicio DESC")
	List<Asignacion> historialDe(Long staffId);

	Optional<Asignacion> findByIdAndStaffId(Long id, Long staffId);

	boolean existsByStaffIdAndUnidadVentaId(Long staffId, Long unidadVentaId);

	@Query("SELECT a.staff FROM Asignacion a WHERE a.unidadVenta.id = :unidadId")
	List<Staff> staffDeLaUnidad(Long unidadId);
}
