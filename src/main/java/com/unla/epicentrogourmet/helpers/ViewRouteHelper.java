package com.unla.epicentrogourmet.helpers;

public final class ViewRouteHelper {

	private ViewRouteHelper() {
	}

	// Home / login
	public static final String HOME_INDEX = "home/index";
	public static final String USER_LOGIN = "user/login";

	// Registro público de unidad + responsable
	public static final String UNIDAD_REGISTRO = "unidad/registro";

	// Administrador
	public static final String ADMIN_FESTIVALES = "admin/festivales";
	public static final String ADMIN_UNIDADES = "admin/unidades";
	public static final String ADMIN_RANKING = "admin/ranking";

	// Responsable
	public static final String RESP_HOME = "responsable/index";
	public static final String RESP_EMPLEADOS = "responsable/empleados";
	public static final String RESP_EMPLEADO_FORM = "responsable/empleado-form";
	public static final String RESP_PLATOS = "responsable/platos";
	public static final String RESP_PLATO_FORM = "responsable/plato-form";
	public static final String RESP_RECAUDACION = "responsable/recaudacion";

	// Empleado
	public static final String EMP_HOME = "empleado/index";

	// Redirecciones
	public static final String ROOT = "/";
	public static final String ADMIN_ROOT = "/admin";
	public static final String RESP_ROOT = "/responsable";
	public static final String RESP_EMPLEADOS_ROOT = "/responsable/empleados";
	public static final String RESP_PLATOS_ROOT = "/responsable/platos";
	public static final String EMP_ROOT = "/empleado";
}
