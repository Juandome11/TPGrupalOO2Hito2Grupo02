package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;
import java.util.List;

public class PedidoDTO {
    private Long unidadId;
    private LocalDate fecha;
    private List<DetallePedidoDTO> detalles;

    public PedidoDTO() {}

    public PedidoDTO(Long unidadId, LocalDate fecha, List<DetallePedidoDTO> detalles) {
        this.unidadId = unidadId;
        this.fecha = fecha;
        this.detalles = detalles;
    }

    // Getters y Setters
    public Long getUnidadId() { return unidadId; }
    public void setUnidadId(Long unidadId) { this.unidadId = unidadId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public List<DetallePedidoDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoDTO> detalles) { this.detalles = detalles; }

    public static class DetallePedidoDTO {
        private Long platoId;
        private int cantidad;

        public DetallePedidoDTO() {}

        public DetallePedidoDTO(Long platoId, int cantidad) {
            this.platoId = platoId;
            this.cantidad = cantidad;
        }

        public Long getPlatoId() { return platoId; }
        public void setPlatoId(Long platoId) { this.platoId = platoId; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    }
}