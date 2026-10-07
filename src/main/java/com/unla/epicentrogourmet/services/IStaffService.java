package com.unla.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;

import com.unla.epicentrogourmet.dtos.EmpleadoDTO;
import com.unla.epicentrogourmet.entities.Staff;

public interface IStaffService {

	/** mailEnviado = false cuando el empleado se creó pero no se pudo enviar el mail con sus credenciales. */
	record ResultadoAlta(Staff empleado, boolean mailEnviado) {
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
}
