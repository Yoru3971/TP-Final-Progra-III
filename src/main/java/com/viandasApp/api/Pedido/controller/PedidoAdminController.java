package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.*;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
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
import org.springframework.http.HttpStatus;
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
@Tag(name = "Pedidos - Admin")
@RequestMapping("/api/admin/pedidos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PedidoAdminController {

    private final PedidoService pedidoService;
    private final PagedResourcesAssembler<PedidoDTO> pagedResourcesAssembler;

    //--------------------------Create--------------------------//
    @Operation(
            summary = "Crear un nuevo pedido",
            description = "Permite a un administrador crear un nuevo pedido para cualquier usuario",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> createPedido(@Valid @RequestBody PedidoCreateDTO pedidoCreateDTO) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        PedidoDTO pedidoCreado = pedidoService.createPedido(pedidoCreateDTO, autenticado);

        Map<String, Object> response = new HashMap<>();
        response.put("Pedido creado correctamente:", pedidoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
      }
  
    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener pedidos paginados y filtrados",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PedidoAdminDTO>>> getPedidos(
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) String emprendimiento,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @PageableDefault(size = 10) Pageable pageable,
            PagedResourcesAssembler<Pedido> pagedResourcesAssembler
    ) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Page<Pedido> pagePedidos = pedidoService.buscarEntidadesPedidos(autenticado, estado, emprendimiento, desde, hasta, pageable);
        PagedModel<EntityModel<PedidoAdminDTO>> pagedModel = pagedResourcesAssembler.toModel(pagePedidos, pedido -> {
            PedidoAdminDTO dto = new PedidoAdminDTO(pedido);

            dto.add(linkTo(methodOn(PedidoAdminController.class).getPedidoPorId(pedido.getId())).withSelfRel());
            return EntityModel.of(dto);
        });

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(
            summary = "Obtener un pedido por ID",
            description = "Obtiene un pedido específico por su ID",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getPedidoPorId(@PathVariable Long id) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<PedidoDTO> pedido = pedidoService.getPedidoById(id, autenticado);
        return ResponseEntity.ok(pedido);
    }

    @Operation(
            summary = "Obtener nombres de emprendimientos de los pedidos",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombres obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/filtros/emprendimientos")
    public ResponseEntity<List<String>> getFiltrosEmprendimientos() {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(pedidoService.getNombresEmprendimientosFiltro(autenticado));
    }

    //--------------------------Update--------------------------//
    @Operation(
            summary = "Actualizar un pedido por ID",
            description = "Permite a un administrador actualizar un pedido específico por su ID",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePedido(@PathVariable Long id, @RequestBody @Valid UpdatePedidoDTO updatePedidoDTO) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<PedidoDTO> pedidoExistente = pedidoService.getPedidoById(id, autenticado);
        Map<String, Object> response = new HashMap<>();

        Optional<PedidoDTO> pedidoActualizar = pedidoService.updatePedidoAdmin(id, updatePedidoDTO);
        response.put("Pedido actualizado correctamente",pedidoActualizar);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar viandas de un pedido por ID",
            description = "Permite a un administrador actualizar las viandas de un pedido específico por su ID",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Viandas del pedido actualizadas correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "Pedido o vianda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}/viandas")
    public ResponseEntity<?> updateViandasPedido(@PathVariable Long id, @Valid @RequestBody PedidoUpdateViandasDTO dto) {
        Map<String, Object> response = new HashMap<>();

        Optional<PedidoDTO> pedidoActualizar = pedidoService.updateViandasPedidoAdmin(id, dto);
        response.put("Pedido actualizado correctamente",pedidoActualizar);
        return ResponseEntity.ok(response);
    }

    //--------------------------Delete--------------------------//
    @Operation(
            summary = "Eliminar un pedido por ID",
            description = "Permite a un administrador eliminar un pedido específico por su ID",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>>  deletePedido(@PathVariable Long id) {

        Map<String, String> response = new HashMap<>();

        pedidoService.deletePedidoAdmin(id);
        response.put("message", "Pedido eliminado correctamente");
        return ResponseEntity.ok(response);
    }
}