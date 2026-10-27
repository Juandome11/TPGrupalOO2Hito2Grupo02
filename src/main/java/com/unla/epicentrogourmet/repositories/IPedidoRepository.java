package com.unla.epicentrogourmet.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.Pedido;

@Repository("pedidoRepository")
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

	@Query("SELECT SUM(d.cantidad * pl.precioVenta) FROM DetallePedido d JOIN d.plato pl JOIN d.pedido pe "
			+ "WHERE pe.unidadVenta.id = :unidadId AND pe.festival.id = :festivalId")
	BigDecimal sumarRecaudacion(@Param("unidadId") Long unidadId, @Param("festivalId") Long festivalId);

	@Query("SELECT SUM(d.cantidad * pl.precioVenta) FROM DetallePedido d JOIN d.plato pl JOIN d.pedido pe "
			+ "WHERE pe.unidadVenta.id = :unidadId AND pe.festival.id = :festivalId AND pe.fecha BETWEEN :desde AND :hasta")
	BigDecimal sumarRecaudacionEntre(@Param("unidadId") Long unidadId, @Param("festivalId") Long festivalId,
									 @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}