package com.unla.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;

import com.unla.epicentrogourmet.dtos.EmpleadoDTO;
import com.unla.epicentrogourmet.entities.Asignacion;
import com.unla.epicentrogourmet.entities.Staff;

public interface IStaffService {

	/**
	 * mailEnviado = false cuando el empleado se creó pero no se pudo enviar el mail con sus credenciales.
	 * reasignado = true cuando el DNI ya era de un empleado que trabajó en un festival anterior: se lo asigna
	 * a la nueva unidad conservando su usuario (no se crea uno nuevo ni se envía mail).
	 */
	record ResultadoAlta(Staff empleado, boolean mailEnviado, boolean reasignado) {
	}

	ResultadoAlta altaEmpleado(String usernameResponsable, EmpleadoDTO dto);

	void modificarEmpleado(String usernameResponsable, EmpleadoDTO dto);

	/** Baja lógica: el empleado queda marcado como eliminado y su usuario deshabilitado. */
	void bajaEmpleado(String usernameResponsable, Long id);

	/** Método de actualización específico para volver a dar de alta un empleado eliminado lógicamente. */
	void reactivarEmpleado(String usernameResponsable, Long id);

	/** Empleados activos de la unidad del responsable, con filtros opcionales (null = sin filtro). */
	List<Staff> buscarActivos(String usernameResponsable, String texto, String dni, String tipo);

	List<Staff> listarEliminados(String usernameResponsable);

	EmpleadoDTO obtenerParaEdicion(String usernameResponsable, Long id);

	Optional<Staff> findByUsername(String username);

	/** Festivales y unidades en las que trabajó el empleado, del más reciente al más antiguo. */
	List<Asignacion> historialDe(Staff empleado);

	/** Una asignación del empleado (null-safe: vacío si no es suya). */
	Optional<Asignacion> findAsignacion(Long asignacionId, Staff empleado);
}
