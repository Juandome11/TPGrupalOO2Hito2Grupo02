package com.unla.epicentrogourmet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.unla.epicentrogourmet.dtos.ReporteRentabilidadDTO;
import com.unla.epicentrogourmet.entities.Cocinero;
import com.unla.epicentrogourmet.entities.Costo;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.FoodTrack;
import com.unla.epicentrogourmet.entities.RolCocina;
import com.unla.epicentrogourmet.entities.Staff;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.repositories.IAsignacionRepository;
import com.unla.epicentrogourmet.repositories.IDetallePedidoRepository;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.services.implementation.LiquidacionService;
import com.unla.epicentrogourmet.services.implementation.RentabilidadService;

class RentabilidadServiceTest {

    private final IPedidoRepository pedidoRepository = mock(IPedidoRepository.class);
    private final IDetallePedidoRepository detallePedidoRepository = mock(IDetallePedidoRepository.class);
    private final IAsignacionRepository asignacionRepository = mock(IAsignacionRepository.class);
    private final RentabilidadService service = new RentabilidadService(pedidoRepository, detallePedidoRepository,
            asignacionRepository, new LiquidacionService());

    private FoodTrack unidad() {
        Festival festival = new Festival("Primavera", "Primavera", LocalDate.of(2026, 9, 20), LocalDate.of(2026, 10, 31));
        festival.setId(1L);
        Costo costo = new Costo();
        costo.setFestival(festival);
        costo.setCostoPorSuperficie(new BigDecimal("500"));
        costo.setPlusElectricidad(new BigDecimal("2000"));
        costo.setSueldoBase(new BigDecimal("100000"));
        costo.setMontoAnioAntiguedad(new BigDecimal("5000"));
        costo.setPlusCocinero(new BigDecimal("150000"));
        festival.setCosto(costo);

        FoodTrack unidad = new FoodTrack();
        unidad.setId(10L);
        unidad.setNombreComercial("Parrilla");
        unidad.setSuperficie(12);
        unidad.setRequiereConexionElectrica(true);
        unidad.setFestival(festival);
        return unidad;
    }

    @Test
    void rentabilidadNetaRestaCostoDePlatosSueldosYCanon() {
        FoodTrack unidad = unidad();

        Staff responsable = new Staff();
        responsable.setFechaIngreso(LocalDate.of(2026, 9, 1));
        Cocinero cocinero = new Cocinero();
        cocinero.setRolCocina(RolCocina.COCINERO);
        cocinero.setFechaIngreso(LocalDate.of(2022, 3, 1));

        when(pedidoRepository.sumarRecaudacion(10L, 1L)).thenReturn(new BigDecimal("68000"));
        when(detallePedidoRepository.sumarCostoProduccion(10L, 1L)).thenReturn(new BigDecimal("26200"));
        when(asignacionRepository.staffDeLaUnidad(10L)).thenReturn(List.of(responsable, cocinero));

        ReporteRentabilidadDTO r = service.rentabilidad(unidad);

        // El festival dura 42 días: (42 / 15 + 1) * 0,5 = 1,5
        // Responsable: 100000 * 1,5 = 150000. Cocinero: (100000 + 150000) * 1,5 = 375000.
        assertThat(r.getGananciaBruta()).isEqualByComparingTo("41800");
        assertThat(r.getSueldos()).isEqualByComparingTo("525000");
        assertThat(r.getCanon()).isEqualByComparingTo("8000"); // 12 * 500 + 2000
        assertThat(r.getRentabilidadNeta()).isEqualByComparingTo("-491200");
    }

    @Test
    void sinPedidosLaRecaudacionYElCostoSonCero() {
        FoodTrack unidad = unidad();
        when(pedidoRepository.sumarRecaudacion(10L, 1L)).thenReturn(null);
        when(detallePedidoRepository.sumarCostoProduccion(10L, 1L)).thenReturn(null);
        when(asignacionRepository.staffDeLaUnidad(10L)).thenReturn(List.of());

        ReporteRentabilidadDTO r = service.rentabilidad(unidad);

        assertThat(r.getRecaudacion()).isEqualByComparingTo("0");
        assertThat(r.getCostoPlatos()).isEqualByComparingTo("0");
        assertThat(r.getRentabilidadNeta()).isEqualByComparingTo("-8000");
    }

    @Test
    void entreFechasSueldosYCanonSeRestanEnProporcionALosDiasDelRango() {
        FoodTrack unidad = unidad();
        Staff responsable = new Staff();
        responsable.setFechaIngreso(LocalDate.of(2026, 9, 1));
        Cocinero cocinero = new Cocinero();
        cocinero.setRolCocina(RolCocina.COCINERO);
        cocinero.setFechaIngreso(LocalDate.of(2022, 3, 1));
        LocalDate desde = LocalDate.of(2026, 9, 20);
        LocalDate hasta = LocalDate.of(2026, 10, 10);

        when(detallePedidoRepository.sumarRecaudacionEntre(10L, 1L, desde, hasta))
                .thenReturn(new BigDecimal("30000"));
        when(detallePedidoRepository.sumarCostoProduccionEntre(10L, 1L, desde, hasta))
                .thenReturn(new BigDecimal("10000"));
        when(asignacionRepository.staffDeLaUnidad(10L)).thenReturn(List.of(responsable, cocinero));

        ReporteRentabilidadDTO r = service.rentabilidad(unidad, desde, hasta);

        assertThat(r.getSueldos()).isEqualByComparingTo("262500");
        assertThat(r.getCanon()).isEqualByComparingTo("4000");
        assertThat(r.getRentabilidadNeta()).isEqualByComparingTo("-246500");
    }

    @Test
    void entreFechasConDesdePosteriorAHastaEsError() {
        assertThatThrownBy(() -> service.rentabilidad(unidad(), LocalDate.of(2026, 10, 10), LocalDate.of(2026, 9, 20)))
                .isInstanceOf(NegocioException.class);
    }

    @Test
    void entreFechasConUnRangoFueraDelFestivalEsError() {
        assertThatThrownBy(() -> service.rentabilidad(unidad(), LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 5)))
                .isInstanceOf(NegocioException.class);
    }
}