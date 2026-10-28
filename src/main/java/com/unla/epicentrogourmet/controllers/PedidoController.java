package com.unla.epicentrogourmet.controllers;

import com.unla.epicentrogourmet.dtos.PedidoDTO;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.services.implementation.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/responsable/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Muestra el formulario web
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("pedidoDTO", new PedidoDTO());
        return "responsable/carga-pedido";
    }

    // Recibe los datos del formulario, aplica las validaciones y captura errores
    @PostMapping("/nuevo")
    public String registrarPedido(@ModelAttribute("pedidoDTO") PedidoDTO pedidoDTO, Model model) {
        try {
            pedidoService.registrarPedido(pedidoDTO);
            model.addAttribute("exito", "¡Pedido registrado correctamente!");
            model.addAttribute("pedidoDTO", new PedidoDTO()); // Limpia el formulario al tener éxito
        } catch (NegocioException e) {
            // Atrapa el mensaje exacto de tus validaciones del Integrante 2
            model.addAttribute("error", e.getMessage());
        }
        return "responsable/carga-pedido";
    }
}