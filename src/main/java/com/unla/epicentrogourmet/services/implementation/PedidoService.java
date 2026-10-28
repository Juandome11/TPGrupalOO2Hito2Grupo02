package com.unla.epicentrogourmet.services.implementation;

import com.unla.epicentrogourmet.dtos.PedidoDTO;
import com.unla.epicentrogourmet.entities.DetallePedido;
import com.unla.epicentrogourmet.entities.Pedido;
import com.unla.epicentrogourmet.entities.Plato;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.repositories.IDetallePedidoRepository;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.repositories.IPlatoRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service("pedidoService")
public class PedidoService {

    @Autowired
    private IPedidoRepository pedidoRepository;

    @Autowired
    private IDetallePedidoRepository detallePedidoRepository; // Repositorio para los ítems

    @Autowired
    private IPlatoRepository platoRepository;

    @Autowired
    private IUnidadVentaRepository unidadVentaRepository;

    public void registrarPedido(PedidoDTO pedidoDTO) throws NegocioException {

        // 1. Validación de fecha actual
        if (pedidoDTO.getFecha() == null || !pedidoDTO.getFecha().equals(LocalDate.now())) {
            throw new NegocioException("La fecha del pedido debe ser obligatoriamente la fecha actual.");
        }

        // 2. Validación de ítems vacíos
        if (pedidoDTO.getDetalles() == null || pedidoDTO.getDetalles().isEmpty() ||
                pedidoDTO.getDetalles().get(0) == null ||
                pedidoDTO.getDetalles().get(0).getPlatoId() == null) {
            throw new NegocioException("El pedido debe contener al menos un plato/ítem.");
        }

        if (pedidoDTO.getUnidadId() == null) {
            throw new NegocioException("Debe especificar una unidad de venta.");
        }

        UnidadVenta unidadVenta = unidadVentaRepository.findById(pedidoDTO.getUnidadId())
                .orElseThrow(() -> new NegocioException("La unidad de venta especificada no existe."));

        // 3. Validación de platos y pertenencia a la misma unidad
        for (PedidoDTO.DetallePedidoDTO detalleDto : pedidoDTO.getDetalles()) {
            if (detalleDto.getPlatoId() == null) {
                throw new NegocioException("El ID del plato no puede estar vacío.");
            }

            Plato plato = platoRepository.findById(detalleDto.getPlatoId())
                    .orElseThrow(() -> new NegocioException("El plato especificado no existe."));

            if (plato.getUnidadVenta() == null || !plato.getUnidadVenta().getId().equals(pedidoDTO.getUnidadId())) {
                throw new NegocioException("Todos los platos del pedido deben pertenecer a la misma unidad de venta.");
            }
        }

        // ----------------------------------------------------
        // GUARDADO DE CABECERA Y DETALLES (Para la Recaudación)
        // ----------------------------------------------------
        Pedido pedido = new Pedido();
        pedido.setFecha(pedidoDTO.getFecha());
        pedido.setUnidadVenta(unidadVenta);
        pedido.setFestival(unidadVenta.getFestival());

        // Guardamos el pedido primero para obtener su ID generado
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Guardamos cada ítem en la tabla detalle_pedido
        for (PedidoDTO.DetallePedidoDTO detalleDto : pedidoDTO.getDetalles()) {
            Plato plato = platoRepository.findById(detalleDto.getPlatoId()).get();

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setPlato(plato);
            //detalle.setCantidad(detalleDto.getCantidad() != null ? detalleDto.getCantidad() : 1);
            detalle.setCantidad(detalleDto.getCantidad() > 0 ? detalleDto.getCantidad() : 1);
            detallePedidoRepository.save(detalle);
        }
    }
}