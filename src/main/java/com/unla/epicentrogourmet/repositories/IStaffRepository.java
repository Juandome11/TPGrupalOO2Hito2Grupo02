package com.unla.epicentrogourmet.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.Staff;

@Repository("staffRepository")
public interface IStaffRepository extends JpaRepository<Staff, Long> {

	boolean existsByDni(String dni);

	boolean existsByEmail(String email);

	Optional<Staff> findByDni(String dni);

	Optional<Staff> findByEmail(String email);

	@Query("SELECT s FROM Staff s WHERE s.user.username = :username")
	Optional<Staff> findByUsername(String username);

	/**
	 * Empleados (Cocinero o Cajero) activos de una unidad. Los filtros son opcionales:
	 * un parámetro nulo no filtra. tipo = "COCINERO" | "CAJERO".
	 */
	@Query("SELECT s FROM Staff s WHERE s.unidadVenta.id = :unidadId AND s.eliminado = false "
			+ "AND (TYPE(s) = Cocinero OR TYPE(s) = Cajero) "
			+ "AND (:texto IS NULL OR LOWER(s.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) "
			+ "     OR LOWER(s.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))) "
			+ "AND (:dni IS NULL OR s.dni = :dni) "
			+ "AND (:tipo IS NULL OR (:tipo = 'COCINERO' AND TYPE(s) = Cocinero) "
			+ "     OR (:tipo = 'CAJERO' AND TYPE(s) = Cajero)) "
			+ "ORDER BY s.apellido, s.nombre")
	List<Staff> buscarEmpleadosActivos(Long unidadId, String texto, String dni, String tipo);

	@Query("SELECT s FROM Staff s WHERE s.unidadVenta.id = :unidadId AND s.eliminado = true "
			+ "AND (TYPE(s) = Cocinero OR TYPE(s) = Cajero) ORDER BY s.apellido, s.nombre")
	List<Staff> listarEmpleadosEliminados(Long unidadId);
}
