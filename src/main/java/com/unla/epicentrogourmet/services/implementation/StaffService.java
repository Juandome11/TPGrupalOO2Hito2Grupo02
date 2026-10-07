package com.unla.epicentrogourmet.services.implementation;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.EmpleadoDTO;
import com.unla.epicentrogourmet.entities.Asignacion;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.Cocinero;
import com.unla.epicentrogourmet.entities.Staff;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.entities.User;
import com.unla.epicentrogourmet.entities.UserRole;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.helpers.ValidacionHelper;
import com.unla.epicentrogourmet.repositories.IAsignacionRepository;
import com.unla.epicentrogourmet.repositories.IStaffRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.repositories.IUserRepository;
import com.unla.epicentrogourmet.services.IEmailService;
import com.unla.epicentrogourmet.services.IStaffService;

@Service("staffService")
public class StaffService implements IStaffService {

	private static final String ALFABETO_PASSWORD = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
	private static final int LONGITUD_PASSWORD = 10;

	private final IStaffRepository staffRepository;
	private final IAsignacionRepository asignacionRepository;
	private final IUnidadVentaRepository unidadVentaRepository;
	private final IUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final IEmailService emailService;
	private final SecureRandom random = new SecureRandom();

	public StaffService(IStaffRepository staffRepository, IAsignacionRepository asignacionRepository,
			IUnidadVentaRepository unidadVentaRepository, IUserRepository userRepository,
			PasswordEncoder passwordEncoder, IEmailService emailService) {
		this.staffRepository = staffRepository;
		this.asignacionRepository = asignacionRepository;
		this.unidadVentaRepository = unidadVentaRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
	}

	@Override
	@Transactional
	public ResultadoAlta altaEmpleado(String usernameResponsable, EmpleadoDTO dto) {
		UnidadVenta unidad = unidadDe(usernameResponsable);
		Optional<Staff> conMismoDni = staffRepository.findByDni(dto.getDni());
		if (conMismoDni.isPresent() && esDeUnFestivalAnterior(conMismoDni.get(), unidad)) {
			return reasignar(conMismoDni.get(), unidad);
		}
		validar(dto, null, unidad);

		Staff empleado = crearSegunTipo(dto);
		copiarDatosPersonales(dto, empleado);
		empleado.setUnidadVenta(unidad);

		String passwordGenerada = generarPassword();
		User user = new User(dto.getEmail(), passwordEncoder.encode(passwordGenerada), true);
		user.agregarRol(UserRole.EMPLEADO);
		empleado.setUser(user);
		empleado = staffRepository.save(empleado);
		asignacionRepository.save(new Asignacion(empleado, unidad));

		boolean mailEnviado = emailService.enviarCredencialesEmpleado(dto.getEmail(), dto.getNombre(), dto.getEmail(),
				passwordGenerada);
		return new ResultadoAlta(empleado, mailEnviado, false);
	}

	/** Un empleado activo cuyo festival ya terminó puede volver a trabajar en otra unidad. */
	private boolean esDeUnFestivalAnterior(Staff staff, UnidadVenta destino) {
		boolean esEmpleado = staff instanceof Cocinero || staff instanceof Cajero;
		UnidadVenta actual = staff.getUnidadVenta();
		return esEmpleado && !staff.isEliminado() && actual != null && !actual.getId().equals(destino.getId())
				&& actual.getFestival().getFechaFin().isBefore(LocalDate.now());
	}

	private ResultadoAlta reasignar(Staff empleado, UnidadVenta destino) {
		empleado.setUnidadVenta(destino);
		staffRepository.save(empleado);
		if (!asignacionRepository.existsByStaffIdAndUnidadVentaId(empleado.getId(), destino.getId())) {
			asignacionRepository.save(new Asignacion(empleado, destino));
		}
		return new ResultadoAlta(empleado, false, true);
	}

	@Override
	@Transactional
	public void modificarEmpleado(String usernameResponsable, EmpleadoDTO dto) {
		UnidadVenta unidad = unidadDe(usernameResponsable);
		Staff empleado = empleadoDeLaUnidad(unidad, dto.getId());
		if (empleado.isEliminado()) {
			throw new NegocioException("El empleado está dado de baja: reactivalo antes de modificarlo.");
		}
		dto.setTipo(empleado instanceof Cocinero ? EmpleadoDTO.COCINERO : EmpleadoDTO.CAJERO);
		validar(dto, empleado, unidad);

		copiarDatosPersonales(dto, empleado);
		if (empleado instanceof Cocinero cocinero) {
			cocinero.setRolCocina(dto.getRolCocina());
		} else if (empleado instanceof Cajero cajero) {
			cajero.setTurno(dto.getTurno());
		}
		empleado.getUser().setUsername(dto.getEmail());
		staffRepository.save(empleado);
	}

	@Override
	@Transactional
	public void bajaEmpleado(String usernameResponsable, Long id) {
		Staff empleado = empleadoDeLaUnidad(unidadDe(usernameResponsable), id);
		empleado.setEliminado(true);
		empleado.getUser().setEnabled(false);
		staffRepository.save(empleado);
	}

	@Override
	@Transactional
	public void reactivarEmpleado(String usernameResponsable, Long id) {
		Staff empleado = empleadoDeLaUnidad(unidadDe(usernameResponsable), id);
		empleado.setEliminado(false);
		empleado.getUser().setEnabled(true);
		staffRepository.save(empleado);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Staff> buscarActivos(String usernameResponsable, String texto, String dni, String tipo) {
		UnidadVenta unidad = unidadDe(usernameResponsable);
		return staffRepository.buscarEmpleadosActivos(unidad.getId(), vacioANull(texto), vacioANull(dni),
				vacioANull(tipo));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Staff> listarEliminados(String usernameResponsable) {
		return staffRepository.listarEmpleadosEliminados(unidadDe(usernameResponsable).getId());
	}

	@Override
	@Transactional(readOnly = true)
	public EmpleadoDTO obtenerParaEdicion(String usernameResponsable, Long id) {
		Staff empleado = empleadoDeLaUnidad(unidadDe(usernameResponsable), id);
		EmpleadoDTO dto = new EmpleadoDTO();
		dto.setId(empleado.getId());
		dto.setNombre(empleado.getNombre());
		dto.setApellido(empleado.getApellido());
		dto.setDni(empleado.getDni());
		dto.setEmail(empleado.getEmail());
		dto.setFechaNacimiento(empleado.getFechaNacimiento());
		dto.setFechaIngreso(empleado.getFechaIngreso());
		if (empleado instanceof Cocinero cocinero) {
			dto.setTipo(EmpleadoDTO.COCINERO);
			dto.setRolCocina(cocinero.getRolCocina());
		} else if (empleado instanceof Cajero cajero) {
			dto.setTipo(EmpleadoDTO.CAJERO);
			dto.setTurno(cajero.getTurno());
		}
		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Staff> findByUsername(String username) {
		return staffRepository.findByUsername(username);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Asignacion> historialDe(Staff empleado) {
		return asignacionRepository.historialDe(empleado.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Asignacion> findAsignacion(Long asignacionId, Staff empleado) {
		return asignacionRepository.findByIdAndStaffId(asignacionId, empleado.getId());
	}

	private UnidadVenta unidadDe(String usernameResponsable) {
		return unidadVentaRepository.findByResponsableUsername(usernameResponsable)
				.orElseThrow(() -> new NegocioException("No tenés una unidad de venta asignada."));
	}

	private Staff empleadoDeLaUnidad(UnidadVenta unidad, Long id) {
		Staff staff = staffRepository.findById(id).orElseThrow(() -> new NegocioException("El empleado no existe."));
		boolean esDeLaUnidad = staff.getUnidadVenta() != null && staff.getUnidadVenta().getId().equals(unidad.getId());
		boolean esEmpleado = staff instanceof Cocinero || staff instanceof Cajero;
		if (!esDeLaUnidad || !esEmpleado) {
			throw new NegocioException("El empleado no pertenece a tu unidad.");
		}
		return staff;
	}

	private void validar(EmpleadoDTO dto, Staff existente, UnidadVenta unidad) {
		ValidacionHelper.validarMayorDeEdad(dto.getFechaNacimiento());
		ValidacionHelper.validarFechaIngreso(dto.getFechaIngreso(), dto.getFechaNacimiento());

		if (EmpleadoDTO.COCINERO.equals(dto.getTipo())) {
			if (dto.getRolCocina() == null) {
				throw new NegocioException("Elegí el rol en la cocina.");
			}
		} else if (EmpleadoDTO.CAJERO.equals(dto.getTipo())) {
			if (dto.getTurno() == null) {
				throw new NegocioException("Elegí el turno del cajero.");
			}
		} else {
			throw new NegocioException("El tipo de empleado no es válido.");
		}

		validarUnico(staffRepository.findByDni(dto.getDni()), existente, unidad, "DNI");
		validarUnico(staffRepository.findByEmail(dto.getEmail()), existente, unidad, "email");
		Optional<User> usuario = userRepository.findByUsernameAndFetchUserRolesEagerly(dto.getEmail());
		boolean usuarioDeOtro = usuario.isPresent()
				&& (existente == null || !usuario.get().getId().equals(existente.getUser().getId()));
		if (usuarioDeOtro) {
			throw new NegocioException("Ya existe un usuario con ese email.");
		}
	}

	private void validarUnico(Optional<Staff> otro, Staff existente, UnidadVenta unidad, String campo) {
		if (otro.isEmpty() || (existente != null && otro.get().getId().equals(existente.getId()))) {
			return;
		}
		Staff duplicado = otro.get();
		boolean bajaEnMiUnidad = duplicado.isEliminado() && duplicado.getUnidadVenta() != null
				&& duplicado.getUnidadVenta().getId().equals(unidad.getId());
		if (bajaEnMiUnidad) {
			throw new NegocioException("Ya existe un empleado con ese " + campo
					+ " dado de baja. Reactivalo desde la lista de empleados eliminados en lugar de crearlo de nuevo.");
		}
		throw new NegocioException("Ya existe una persona registrada con ese " + campo + ".");
	}

	private Staff crearSegunTipo(EmpleadoDTO dto) {
		if (EmpleadoDTO.COCINERO.equals(dto.getTipo())) {
			Cocinero cocinero = new Cocinero();
			cocinero.setRolCocina(dto.getRolCocina());
			return cocinero;
		}
		Cajero cajero = new Cajero();
		cajero.setTurno(dto.getTurno());
		return cajero;
	}

	private void copiarDatosPersonales(EmpleadoDTO dto, Staff empleado) {
		empleado.setNombre(dto.getNombre());
		empleado.setApellido(dto.getApellido());
		empleado.setDni(dto.getDni());
		empleado.setEmail(dto.getEmail());
		empleado.setFechaNacimiento(dto.getFechaNacimiento());
		empleado.setFechaIngreso(dto.getFechaIngreso());
	}

	private String generarPassword() {
		StringBuilder sb = new StringBuilder(LONGITUD_PASSWORD);
		for (int i = 0; i < LONGITUD_PASSWORD; i++) {
			sb.append(ALFABETO_PASSWORD.charAt(random.nextInt(ALFABETO_PASSWORD.length())));
		}
		return sb.toString();
	}

	private String vacioANull(String valor) {
		return valor == null || valor.isBlank() ? null : valor.trim();
	}
}
