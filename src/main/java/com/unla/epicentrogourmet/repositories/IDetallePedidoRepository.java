package com.unla.epicentrogourmet.repositories;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.unla.epicentrogourmet.entities.DetallePedido;

@Repository("detallePedidoRepository")
public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    @Query("SELECT SUM(d.cantidad * pl.costoProduccion) FROM DetallePedido d JOIN d.plato pl JOIN d.pedido pe "
            + "WHERE pe.unidadVenta.id = :unidadId AND pe.festival.id = :festivalId")
    BigDecimal sumarCostoProduccion(Long unidadId, Long festivalId);
}