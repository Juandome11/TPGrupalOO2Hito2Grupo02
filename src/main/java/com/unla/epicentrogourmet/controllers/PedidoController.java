package com.unla.epicentrogourmet.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.unla.epicentrogourmet.dtos.PedidoDTO;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.services.IPlatoService;
import com.unla.epicentrogourmet.services.IUnidadVentaService;
import com.unla.epicentrogourmet.services.implementation.PedidoService;

/** Carga de pedidos del responsable: siempre sobre su propia unidad y con platos de su menú. */
@Controller
@RequestMapping("/responsable/pedidos")
public class PedidoController {

    private static final String VISTA = "responsable/carga-pedido";

    private final PedidoService pedidoService;
    private final IUnidadVentaService unidadVentaService;
    private final IPlatoService platoService;

    public PedidoController(PedidoService pedidoService, IUnidadVentaService unidadVentaService,
            IPlatoService platoService) {
        this.pedidoService = pedidoService;
        this.unidadVentaService = unidadVentaService;
        this.platoService = platoService;
    }

    // Muestra el formulario web, con la fecha de hoy ya cargada
    @GetMapping("/nuevo")
    public String mostrarFormulario(Principal principal, Model model) {
        PedidoDTO pedido = new PedidoDTO();
        pedido.setFecha(LocalDate.now());
        return formulario(principal, model, pedido);
    }

    // Recibe los datos del formulario, aplica las validaciones y captura errores
    @PostMapping("/nuevo")
    public String registrarPedido(Principal principal, @ModelAttribute("pedidoDTO") PedidoDTO pedidoDTO,
            BindingResult bindingResult, Model model) {
        Optional<UnidadVenta> unidad = unidadVentaService.findByResponsableUsername(principal.getName());
        if (unidad.isEmpty()) {
            model.addAttribute("error", "No tenés una unidad de venta asignada.");
            return formulario(principal, model, pedidoDTO);
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Revisá los datos ingresados: la fecha y las cantidades tienen que ser válidas.");
            return formulario(principal, model, pedidoDTO);
        }

        // La unidad sale del usuario logueado: lo que venga del formulario no se tiene en cuenta
        pedidoDTO.setUnidadId(unidad.get().getId());
        // El formulario trae filas de más: se descartan las que no tienen plato elegido
        if (pedidoDTO.getDetalles() != null) {
            pedidoDTO.setDetalles(new ArrayList<>(pedidoDTO.getDetalles().stream()
                    .filter(d -> d != null && d.getPlatoId() != null).toList()));
        }
        try {
            pedidoService.registrarPedido(pedidoDTO);
            model.addAttribute("exito", "¡Pedido registrado correctamente!");
            PedidoDTO nuevo = new PedidoDTO();
            nuevo.setFecha(LocalDate.now()); // Limpia el formulario al tener éxito
            return formulario(principal, model, nuevo);
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(principal, model, pedidoDTO);
        }
    }

    private String formulario(Principal principal, Model model, PedidoDTO pedido) {
        model.addAttribute("pedidoDTO", pedido);
        Optional<UnidadVenta> unidad = unidadVentaService.findByResponsableUsername(principal.getName());
        model.addAttribute("unidad", unidad.orElse(null));
        model.addAttribute("platos", unidad.isPresent() ? platoService.listar(principal.getName()) : java.util.List.of());
        return VISTA;
    }
}
