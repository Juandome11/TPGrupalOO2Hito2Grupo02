package com.unla.epicentrogourmet.services.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.ReporteRentabilidadDTO;
import com.unla.epicentrogourmet.entities.Costo;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.Staff;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.repositories.IAsignacionRepository;
import com.unla.epicentrogourmet.repositories.IDetallePedidoRepository;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.services.ILiquidacionService;
import com.unla.epicentrogourmet.services.IRentabilidadService;

@Service("rentabilidadService")
public class RentabilidadService implements IRentabilidadService {

    private final IPedidoRepository pedidoRepository;
    private final IDetallePedidoRepository detallePedidoRepository;
    private final IAsignacionRepository asignacionRepository;
    private final ILiquidacionService liquidacionService;

    public RentabilidadService(IPedidoRepository pedidoRepository, IDetallePedidoRepository detallePedidoRepository,
                               IAsignacionRepository asignacionRepository, ILiquidacionService liquidacionService) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.asignacionRepository = asignacionRepository;
        this.liquidacionService = liquidacionService;
    }

    // --- METODO DEL CASO DE USO 3 ---
    @Override
    @Transactional(readOnly = true)
    public ReporteRentabilidadDTO rentabilidad(UnidadVenta unidad) {
        Festival festival = unidad.getFestival();
        BigDecimal recaudacion = cero(pedidoRepository.sumarRecaudacion(unidad.getId(), festival.getId()));
        BigDecimal costoPlatos = cero(detallePedidoRepository.sumarCostoProduccion(unidad.getId(), festival.getId()));
        return new ReporteRentabilidadDTO(festival.getNombre(), unidad.getNombreComercial(), festival.getFechaInicio(),
                festival.getFechaFin(), recaudacion, costoPlatos, sueldosDe(unidad), canonDe(unidad));
    }

    // --- METODO DEL CASO DE USO 4 ---
    @Override
    @Transactional(readOnly = true)
    public ReporteRentabilidadDTO rentabilidad(UnidadVenta unidad, LocalDate desde, LocalDate hasta) {
        Festival festival = unidad.getFestival();
        if (desde.isAfter(hasta)) {
            throw new NegocioException("La fecha desde no puede ser posterior a la fecha hasta.");
        }
        if (hasta.isBefore(festival.getFechaInicio()) || desde.isAfter(festival.getFechaFin())) {
            throw new NegocioException("El rango elegido no coincide con las fechas del festival.");
        }
        LocalDate inicio = desde.isBefore(festival.getFechaInicio()) ? festival.getFechaInicio() : desde;
        LocalDate fin = hasta.isAfter(festival.getFechaFin()) ? festival.getFechaFin() : hasta;

        BigDecimal recaudacion = cero(
                detallePedidoRepository.sumarRecaudacionEntre(unidad.getId(), festival.getId(), inicio, fin));
        BigDecimal costoPlatos = cero(
                detallePedidoRepository.sumarCostoProduccionEntre(unidad.getId(), festival.getId(), inicio, fin));

        long diasRango = ChronoUnit.DAYS.between(inicio, fin) + 1;
        long duracionTotalFestival = ChronoUnit.DAYS.between(festival.getFechaInicio(), festival.getFechaFin()) + 1;

        BigDecimal proporcion = BigDecimal.valueOf(diasRango)
                .divide(BigDecimal.valueOf(duracionTotalFestival), 10, RoundingMode.HALF_UP);

        BigDecimal sueldos = sueldosDe(unidad).multiply(proporcion).setScale(2, RoundingMode.HALF_UP);
        BigDecimal canon = canonDe(unidad).multiply(proporcion).setScale(2, RoundingMode.HALF_UP);

        return new ReporteRentabilidadDTO(festival.getNombre(), unidad.getNombreComercial(), inicio, fin,
                recaudacion, costoPlatos, sueldos, canon);
    }

    private BigDecimal sueldosDe(UnidadVenta unidad) {
        BigDecimal total = BigDecimal.ZERO;
        for (Staff persona : asignacionRepository.staffDeLaUnidad(unidad.getId())) {
            total = total.add(liquidacionService.calcular(persona, unidad).getTotalACobrar());
        }
        return total;
    }

    private BigDecimal canonDe(UnidadVenta unidad) {
        Costo costo = unidad.getFestival().getCosto();
        if (costo == null) {
            throw new NegocioException("El festival no tiene costos configurados, no se puede calcular el canon.");
        }
        return unidad.calcularCanon(costo);
    }

    private BigDecimal cero(BigDecimal monto) {
        return monto == null ? BigDecimal.ZERO : monto;
    }
}
