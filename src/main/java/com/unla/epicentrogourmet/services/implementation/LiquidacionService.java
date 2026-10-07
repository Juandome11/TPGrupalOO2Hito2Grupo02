package com.unla.epicentrogourmet.services.implementation;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.unla.epicentrogourmet.dtos.ReciboSueldoDTO;
import com.unla.epicentrogourmet.entities.Cocinero;
import com.unla.epicentrogourmet.entities.Costo;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.Staff;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.services.ILiquidacionService;

/**
 * Liquidación de haberes. Todos los montos salen de la entidad Costo del festival (nada hardcodeado).
 * Cocina: (sueldo base + plus por categoría). Resto del personal (cajeros y responsable): (sueldo base + antigüedad).
 * El porcentaje depende de la duración del festival: hasta 15 días 50%, de 16 a 30 días 100%,
 * más de 30 días (int(días / 15) + 1) * 50%.
 */
@Service("liquidacionService")
public class LiquidacionService implements ILiquidacionService {

	private static final long DIAS_TRAMO_CORTO = 15;
	private static final long DIAS_TRAMO_MEDIO = 30;
	private static final BigDecimal MEDIO = new BigDecimal("0.5");

	@Override
	public ReciboSueldoDTO calcular(Staff empleado, UnidadVenta unidadVenta) {
		Festival festival = unidadVenta.getFestival();
		Costo costo = festival.getCosto();
		if (costo == null) {
			throw new NegocioException("El festival \"" + festival.getNombre() + "\" no tiene costos configurados.");
		}
		long dias = festival.getDuracionDias();
		BigDecimal porcentaje = porcentajeSegunDias(dias);

		BigDecimal adicional;
		String concepto;
		if (empleado instanceof Cocinero cocinero) {
			adicional = costo.plusPara(cocinero.getRolCocina());
			concepto = "Plus por categoría (" + cocinero.getRolCocina().getDescripcion() + ")";
		} else {
			int anios = empleado.antiguedadAnios(festival.getFechaInicio());
			adicional = costo.getMontoAnioAntiguedad().multiply(BigDecimal.valueOf(anios));
			concepto = "Antigüedad (" + anios + (anios == 1 ? " año)" : " años)");
		}

		BigDecimal total = costo.getSueldoBase().add(adicional).multiply(porcentaje);
		return new ReciboSueldoDTO(empleado.getNombreCompleto(), empleado.getDni(), empleado.getTipo(),
				festival.getNombre(), unidadVenta.getNombreComercial(), festival.getFechaInicio(), festival.getFechaFin(), dias,
				costo.getSueldoBase(), concepto, adicional, porcentaje, total);
	}

	private BigDecimal porcentajeSegunDias(long dias) {
		if (dias <= DIAS_TRAMO_CORTO) {
			return MEDIO;
		}
		if (dias <= DIAS_TRAMO_MEDIO) {
			return BigDecimal.ONE;
		}
		return BigDecimal.valueOf(dias / DIAS_TRAMO_CORTO + 1).multiply(MEDIO);
	}
}
