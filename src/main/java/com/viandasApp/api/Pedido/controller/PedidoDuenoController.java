package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.UpdatePedidoDTO;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.service.PedidoService;
import com.viandasApp.api.Usuario.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@Tag(name = "Pedidos - Dueño")
@RequestMapping("/api/dueno/pedidos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DUENO')")
public class PedidoDuenoController {

    private final PedidoService pedidoService;
    private final PagedResourcesAssembler<PedidoDTO> pagedResourcesAssembler;

    //--------------------------Update--------------------------//
     @Operation(
            summary = "Actualizar pedido por ID",
            description = "Actualiza la información de un pedido específico por su ID",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido actualizado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> updatePedido(@PathVariable Long id, @RequestBody @Valid UpdatePedidoDTO updatePedidoDTO) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<PedidoDTO> pedidoActualizado = pedidoService.updatePedidoDueno(id, updatePedidoDTO, autenticado);

        Map<String, Object> response = new HashMap<>();
        response.put("Pedido actualizado correctamente:", pedidoActualizado);
        return ResponseEntity.ok(response);
    }

    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener pedidos del dueño autenticado con paginación y filtros",
            description = "Permite a un dueño obtener todos sus pedidos filtrados",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PedidoDTO>>> getPedidos(
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) String emprendimiento, // Nombre del emprendimiento
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Page<PedidoDTO> page = pedidoService.buscarPedidos(autenticado, estado, emprendimiento, desde, hasta, pageable);

        PagedModel<EntityModel<PedidoDTO>> pagedModel = pagedResourcesAssembler.toModel(page, pedido -> {
            pedido.add(linkTo(methodOn(PedidoAdminController.class).getPedidoPorId(pedido.getId())).withSelfRel());
            return EntityModel.of(pedido);
        });

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(
            summary = "Obtener nombres de emprendimientos de los pedidos",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombres obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/filtros/emprendimientos")
    public ResponseEntity<List<String>> getFiltrosEmprendimientos() {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(pedidoService.getNombresEmprendimientosFiltro(autenticado));
    }

    @Operation(
            summary = "Obtener un pedido por ID",
            description = "Obtiene un pedido específico por su ID",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> getById(@PathVariable Long id) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(pedidoService.getPedidoById(id, autenticado).orElseThrow());
    }

}
