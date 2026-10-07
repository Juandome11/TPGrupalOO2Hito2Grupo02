package com.unla.epicentrogourmet.repositories;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.Pedido;

@Repository("pedidoRepository")
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

	/** Suma de cantidad x precio de venta de todos los pedidos de una unidad en un festival (null si no hay). */
	@Query("SELECT SUM(d.cantidad * pl.precioVenta) FROM DetallePedido d JOIN d.plato pl JOIN d.pedido pe "
			+ "WHERE pe.unidadVenta.id = :unidadId AND pe.festival.id = :festivalId")
	BigDecimal sumarRecaudacion(Long unidadId, Long festivalId);
}
