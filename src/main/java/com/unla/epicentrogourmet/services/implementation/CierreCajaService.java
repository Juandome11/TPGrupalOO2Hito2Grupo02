package com.unla.epicentrogourmet.services.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.ResumenCajaDTO;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.CierreCaja;
import com.unla.epicentrogourmet.entities.Staff;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.repositories.ICierreCajaRepository;
import com.unla.epicentrogourmet.repositories.IDetallePedidoRepository;
import com.unla.epicentrogourmet.services.ICierreCajaService;

/**
 * Cierre de caja del cajero. El total es lo que recaudó su unidad de venta en el día (pedidos con la fecha de hoy).
 * Cada cajero cierra su caja una sola vez por día; una vez cerrada, el total queda fijo aunque se carguen más pedidos.
 */
@Service("cierreCajaService")
public class CierreCajaService implements ICierreCajaService {

	private final ICierreCajaRepository cierreCajaRepository;
	private final IDetallePedidoRepository detallePedidoRepository;

	public CierreCajaService(ICierreCajaRepository cierreCajaRepository,
			IDetallePedidoRepository detallePedidoRepository) {
		this.cierreCajaRepository = cierreCajaRepository;
		this.detallePedidoRepository = detallePedidoRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public ResumenCajaDTO resumenDelDia(Staff empleado) {
		Cajero cajero = cajeroDe(empleado);
		UnidadVenta unidad = unidadDe(cajero);
		LocalDate hoy = LocalDate.now();
		Optional<CierreCaja> cierre = cierreCajaRepository.findByCajeroIdAndFecha(cajero.getId(), hoy);
		if (cierre.isPresent()) {
			return new ResumenCajaDTO(hoy, unidad.getNombreComercial(), cierre.get().getTotal(), true,
					cierre.get().getCerradaEn());
		}
		return new ResumenCajaDTO(hoy, unidad.getNombreComercial(), recaudacionDelDia(unidad, hoy), false, null);
	}

	@Override
	@Transactional
	public CierreCaja cerrar(Staff empleado) {
		Cajero cajero = cajeroDe(empleado);
		UnidadVenta unidad = unidadDe(cajero);
		LocalDate hoy = LocalDate.now();
		if (cierreCajaRepository.findByCajeroIdAndFecha(cajero.getId(), hoy).isPresent()) {
			throw new NegocioException("Ya cerraste la caja de hoy.");
		}
		try {
			return cierreCajaRepository
					.saveAndFlush(new CierreCaja(cajero, unidad, hoy, recaudacionDelDia(unidad, hoy)));
		} catch (DataIntegrityViolationException e) {
			// doble click o dos pestañas: la restricción única (cajero, fecha) lo frena
			throw new NegocioException("Ya cerraste la caja de hoy.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CierreCaja> historial(Staff empleado) {
		return cierreCajaRepository.findByCajeroIdOrderByFechaDesc(cajeroDe(empleado).getId());
	}

	private Cajero cajeroDe(Staff empleado) {
		if (empleado instanceof Cajero cajero) {
			return cajero;
		}
		throw new NegocioException("Solo los cajeros pueden cerrar la caja.");
	}

	private UnidadVenta unidadDe(Cajero cajero) {
		if (cajero.getUnidadVenta() == null) {
			throw new NegocioException("No tenés una unidad de venta asignada.");
		}
		return cajero.getUnidadVenta();
	}

	private BigDecimal recaudacionDelDia(UnidadVenta unidad, LocalDate dia) {
		BigDecimal total = detallePedidoRepository.sumarRecaudacionEntre(unidad.getId(), unidad.getFestival().getId(),
				dia, dia);
		return total == null ? BigDecimal.ZERO : total;
	}
}
