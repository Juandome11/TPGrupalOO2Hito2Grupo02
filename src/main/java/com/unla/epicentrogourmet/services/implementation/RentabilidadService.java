package com.unla.epicentrogourmet.services.implementation;

import java.math.BigDecimal;
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

    @Override
    @Transactional(readOnly = true)
    public ReporteRentabilidadDTO rentabilidad(UnidadVenta unidad) {
        Festival festival = unidad.getFestival();
        BigDecimal recaudacion = cero(pedidoRepository.sumarRecaudacion(unidad.getId(), festival.getId()));
        BigDecimal costoPlatos = cero(detallePedidoRepository.sumarCostoProduccion(unidad.getId(), festival.getId()));
        return new ReporteRentabilidadDTO(festival.getNombre(), unidad.getNombreComercial(), festival.getFechaInicio(),
                festival.getFechaFin(), recaudacion, costoPlatos, sueldosDe(unidad), canonDe(unidad));
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