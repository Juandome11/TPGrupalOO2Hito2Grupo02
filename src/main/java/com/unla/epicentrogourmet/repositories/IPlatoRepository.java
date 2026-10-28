package com.unla.epicentrogourmet.repositories;

import java.util.List;
import java.util.Optional;

import com.unla.epicentrogourmet.dtos.PlatoRankingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.Plato;

@Repository("platoRepository")
public interface IPlatoRepository extends JpaRepository<Plato, Long> {

    List<Plato> findByUnidadVentaIdOrderByNombre(Long unidadVentaId);

    @Query("SELECT p FROM DetallePedido dp " +
            "JOIN dp.pedido pe " +
            "JOIN dp.plato p " +
            "WHERE pe.unidadVenta.id = :unidadId AND pe.festival.id = :festivalId " +
            "GROUP BY p.id " +
            "ORDER BY SUM(dp.cantidad) DESC")
    List<Plato> findPlatoEstrella(@Param("unidadId") Long unidadId, @Param("festivalId") Long festivalId);
    @Query("SELECT new com.unla.epicentrogourmet.dtos.PlatoRankingDTO(" +
            "p.id, p.nombre, p.precioVenta, p.costoProduccion, SUM(dp.cantidad), " +
            "SUM(dp.cantidad * p.precioVenta)) " +
            "FROM DetallePedido dp " +
            "JOIN dp.pedido pe " +
            "JOIN dp.plato p " +
            "WHERE pe.unidadVenta.id = :unidadId AND pe.festival.id = :festivalId " +
            "GROUP BY p.id, p.nombre, p.precioVenta, p.costoProduccion " +
            "ORDER BY SUM(dp.cantidad) DESC")
    List<PlatoRankingDTO> obtenerRankingPlatos(@Param("unidadId") Long unidadId, @Param("festivalId") Long festivalId);
}