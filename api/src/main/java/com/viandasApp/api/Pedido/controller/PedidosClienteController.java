package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.service.PedidoService;
import com.viandasApp.api.Usuario.service.UsuarioServiceImpl;
import com.viandasApp.api.Vianda.service.ViandaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cliente/pedidos")
@RequiredArgsConstructor
public class PedidosClienteController {

    private final PedidoService pedidoService;
    private final UsuarioServiceImpl usuarioService;
    private final ViandaServiceImpl viandaService;




}
