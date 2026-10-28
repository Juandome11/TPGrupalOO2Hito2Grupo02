package com.unla.epicentrogourmet.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.DetallePedido;

@Repository("detallePedidoRepository")
public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    @Query("SELECT SUM(dp.cantidad * dp.plato.costoProduccion) FROM DetallePedido dp "
            + "JOIN dp.pedido pe WHERE pe.unidadVenta.id = :unidadId AND pe.festival.id = :festivalId")
    BigDecimal sumarCostoProduccion(@Param("unidadId") Long unidadId, @Param("festivalId") Long festivalId);

    @Query("SELECT SUM(dp.cantidad * dp.plato.costoProduccion) FROM DetallePedido dp "
            + "JOIN dp.pedido pe WHERE pe.unidadVenta.id = :unidadId AND pe.festival.id = :festivalId "
            + "AND pe.fecha BETWEEN :desde AND :hasta")
    BigDecimal sumarCostoProduccionEntre(@Param("unidadId") Long unidadId, @Param("festivalId") Long festivalId,
                                         @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT SUM(dp.cantidad * dp.plato.precioVenta) FROM DetallePedido dp "
            + "JOIN dp.pedido pe WHERE pe.unidadVenta.id = :unidadId AND pe.festival.id = :festivalId "
            + "AND pe.fecha BETWEEN :desde AND :hasta")
    BigDecimal sumarRecaudacionEntre(@Param("unidadId") Long unidadId, @Param("festivalId") Long festivalId,
                                     @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}