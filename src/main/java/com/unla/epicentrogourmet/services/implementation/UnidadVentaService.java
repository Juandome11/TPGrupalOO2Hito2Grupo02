package com.unla.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.RegistroUnidadResponsableDTO;
import com.unla.epicentrogourmet.entities.Desarmable;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.FoodTrack;
import com.unla.epicentrogourmet.entities.Staff;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.entities.User;
import com.unla.epicentrogourmet.entities.UserRole;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.helpers.CodigoUnidadHelper;
import com.unla.epicentrogourmet.helpers.ValidacionHelper;
import com.unla.epicentrogourmet.repositories.IFestivalRepository;
import com.unla.epicentrogourmet.repositories.IStaffRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.repositories.IUserRepository;
import com.unla.epicentrogourmet.services.IUnidadVentaService;

@Service("unidadVentaService")
public class UnidadVentaService implements IUnidadVentaService {

	private final IUnidadVentaRepository unidadVentaRepository;
	private final IFestivalRepository festivalRepository;
	private final IStaffRepository staffRepository;
	private final IUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UnidadVentaService(IUnidadVentaRepository unidadVentaRepository, IFestivalRepository festivalRepository,
			IStaffRepository staffRepository, IUserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.unidadVentaRepository = unidadVentaRepository;
		this.festivalRepository = festivalRepository;
		this.staffRepository = staffRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public UnidadVenta registrarConResponsable(RegistroUnidadResponsableDTO dto) {
		Festival festival = festivalRepository.findById(dto.getFestivalId())
				.orElseThrow(() -> new NegocioException("El festival elegido no existe."));
		if (festival.getFechaFin().isBefore(LocalDate.now())) {
			throw new NegocioException("El festival \"" + festival.getNombre() + "\" ya finalizó.");
		}

		boolean foodTruck = RegistroUnidadResponsableDTO.FOOD_TRUCK.equals(dto.getTipo());
		if (!foodTruck && !RegistroUnidadResponsableDTO.DESARMABLE.equals(dto.getTipo())) {
			throw new NegocioException("El tipo de unidad no es válido.");
		}
		validarDatosDeLaUnidad(dto, foodTruck);
		validarDatosDelResponsable(dto);

		Staff responsable = new Staff();
		responsable.setNombre(dto.getNombre());
		responsable.setApellido(dto.getApellido());
		responsable.setDni(dto.getDni());
		responsable.setEmail(dto.getEmail());
		responsable.setFechaNacimiento(dto.getFechaNacimiento());
		responsable.setFechaIngreso(LocalDate.now());

		User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), true);
		user.agregarRol(UserRole.RESPONSABLE);
		responsable.setUser(user);
		responsable = staffRepository.save(responsable);

		UnidadVenta unidad = foodTruck ? crearFoodTruck(dto) : crearDesarmable(dto);
		unidad.setNombreComercial(dto.getNombreComercial());
		unidad.setSuperficie(dto.getSuperficie());
		unidad.setCodigo(dto.getCodigo());
		unidad.setFestival(festival);
		unidad.setResponsable(responsable);
		unidad = unidadVentaRepository.save(unidad);

		responsable.setUnidadVenta(unidad);
		staffRepository.save(responsable);
		return unidad;
	}

	private void validarDatosDeLaUnidad(RegistroUnidadResponsableDTO dto, boolean foodTruck) {
		if (!CodigoUnidadHelper.esValido(dto.getCodigo(), foodTruck)) {
			String prefijo = foodTruck ? CodigoUnidadHelper.PREFIJO_FOOD_TRUCK : CodigoUnidadHelper.PREFIJO_DESARMABLE;
			throw new NegocioException("El código debe tener el formato " + prefijo + "-NNNNNNN (7 dígitos), por ejemplo "
					+ prefijo + "-1234567.");
		}
		if (unidadVentaRepository.existsByCodigo(dto.getCodigo())) {
			throw new NegocioException("Ya existe una unidad con ese código.");
		}
		if (foodTruck) {
			if (dto.getPatente() == null || dto.getPatente().isBlank()) {
				throw new NegocioException("La patente es obligatoria para un Food Truck.");
			}
		} else if (dto.getCantCarpas() == null || dto.getTiempoMontaje() == null) {
			throw new NegocioException("La cantidad de carpas y el tiempo de montaje son obligatorios para un Puesto Desarmable.");
		}
	}

	private void validarDatosDelResponsable(RegistroUnidadResponsableDTO dto) {
		ValidacionHelper.validarMayorDeEdad(dto.getFechaNacimiento());
		if (staffRepository.existsByDni(dto.getDni())) {
			throw new NegocioException("Ya existe una persona registrada con ese DNI.");
		}
		if (staffRepository.existsByEmail(dto.getEmail())) {
			throw new NegocioException("Ya existe una persona registrada con ese email.");
		}
		if (userRepository.existsByUsername(dto.getUsername())) {
			throw new NegocioException("El usuario \"" + dto.getUsername() + "\" ya está en uso.");
		}
	}

	private FoodTrack crearFoodTruck(RegistroUnidadResponsableDTO dto) {
		FoodTrack foodTrack = new FoodTrack();
		foodTrack.setPatente(dto.getPatente());
		foodTrack.setRequiereConexionElectrica(dto.isRequiereConexionElectrica());
		return foodTrack;
	}

	private Desarmable crearDesarmable(RegistroUnidadResponsableDTO dto) {
		Desarmable desarmable = new Desarmable();
		desarmable.setCantCarpas(dto.getCantCarpas());
		desarmable.setTiempoMontaje(dto.getTiempoMontaje());
		return desarmable;
	}

	@Override
	public List<UnidadVenta> findByFestival(Long festivalId) {
		return unidadVentaRepository.findByFestivalId(festivalId);
	}

	@Override
	public Optional<UnidadVenta> findByResponsableUsername(String username) {
		return unidadVentaRepository.findByResponsableUsername(username);
	}
}
