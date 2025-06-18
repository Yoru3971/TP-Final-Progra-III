package com.viandasApp.api.Pedido.controller;

import com.viandasApp.api.Pedido.dto.PedidoCreateDTO;
import com.viandasApp.api.Pedido.dto.PedidoDTO;
import com.viandasApp.api.Pedido.dto.PedidoUpdateViandasDTO;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Pedidos - Admin", description = "Controlador para la gestión de pedidos por parte de administradores")
@RequestMapping("/api/admin/pedidos")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class PedidoAdminController {

    private final PedidoService pedidoService;

    @Operation(
            summary = "Crear un nuevo pedido",
            description = "Permite a un administrador crear un nuevo pedido para cualquier usuario",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> createPedido(@Valid @RequestBody PedidoCreateDTO pedidoCreateDTO, BindingResult result) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        PedidoDTO pedidoDTO = pedidoService.createPedido(pedidoCreateDTO, autenticado);
        if (pedidoDTO == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el pedido. Por favor, intente nuevamente.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTO);
    }

    @Operation(
            summary = "Actualizar un pedido por ID",
            description = "Permite a un administrador actualizar un pedido específico por su ID",
            security = @SecurityRequirement(name = "basicAuth")
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

        Optional<PedidoDTO> pedidoExistente = pedidoService.getPedidoById(id);
        Map<String, Object> response = new HashMap<>();

        if (pedidoExistente.isPresent()) {
            pedidoService.updatePedidoAdmin(id, updatePedidoDTO);
            response.put("message", "Pedido actualizado correctamente");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Pedido no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Actualizar viandas de un pedido por ID",
            description = "Permite a un administrador actualizar las viandas de un pedido específico por su ID",
            security = @SecurityRequirement(name = "basicAuth")
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
    public ResponseEntity<?> updateViandasPedido(@PathVariable Long id, @Valid @RequestBody PedidoUpdateViandasDTO dto, BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        Optional<PedidoDTO> actualizado = pedidoService.updateViandasPedidoAdmin(id, dto);
        if (actualizado.isPresent()) {
            return ResponseEntity.ok(actualizado.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Pedido o vianda no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Eliminar un pedido por ID",
            description = "Permite a un administrador eliminar un pedido específico por su ID",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>> deletePedido(@PathVariable Long id) {

        Optional<PedidoDTO> pedidoEliminar = pedidoService.getPedidoById(id);
        Map<String, String> response = new HashMap<>();

        if (pedidoEliminar.isPresent()) {
            pedidoService.deletePedidoAdmin(id);
            response.put("message", "Pedido eliminado correctamente");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Pedido no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Obtener todos los pedidos",
            description = "Obtiene una lista de todos los pedidos realizados por los usuarios",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<?> getAllPedidos() {
        List<PedidoDTO> pedido = pedidoService.getAllPedidos();

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Obtener un pedido por ID",
            description = "Obtiene un pedido específico por su ID",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getPedidoPorId(@PathVariable Long id) {
        Optional<PedidoDTO> pedido = pedidoService.getPedidoById(id);
        if (pedido.isPresent()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Pedido no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Obtener pedidos por ID de usuario",
            description = "Obtiene todos los pedidos realizados por un usuario específico",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/idUsuario/{idUsuario}")
    public ResponseEntity<?> getPedidosDeUsuario(@PathVariable Long idUsuario) {
        List<PedidoDTO> pedido = pedidoService.getAllPedidosByUsuarioId(idUsuario);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Obtener pedidos por ID de emprendimiento",
            description = "Obtiene todos los pedidos asociados a un emprendimiento específico",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorEmprendimiento(@PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEmprendimiento(idEmprendimiento, autenticado);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Obtener pedidos por ID de emprendimiento y usuario",
            description = "Obtiene todos los pedidos asociados a un emprendimiento específico y un usuario",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/idEmprendimiento/{idEmprendimiento}/idUsuario/{idUsuario}")
    public ResponseEntity<?> getPedidosPorEmprendimientoYUsuario(@PathVariable Long idEmprendimiento,
                                                                 @PathVariable Long idUsuario) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEmprendimientoAndUsuario(idEmprendimiento, idUsuario, autenticado);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Obtener pedidos por estado",
            description = "Obtiene todos los pedidos con un estado específico",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getPedidosPorEstado(@PathVariable EstadoPedido estado) {
        List<PedidoDTO> pedido = pedidoService.getAllPedidosByEstado(estado);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Obtener pedidos por fecha",
            description = "Obtiene todos los pedidos realizados en una fecha específica",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> getPedidosPorFecha(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByFecha(fecha);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Obtener pedidos por fecha y usuario",
            description = "Obtiene todos los pedidos realizados en una fecha específica por un usuario",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/fecha/{fecha}/idUsuario/{idUsuario}")
    public ResponseEntity<?> getPedidosPorFechaAndUsuarioId(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                            @PathVariable Long idUsuario) {

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByFechaAndUsuarioId(fecha, idUsuario);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(
            summary = "Obtener pedidos por fecha y emprendimiento",
            description = "Obtiene todos los pedidos realizados en una fecha y emprendimiento específicos",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol de administrador"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/fecha/{fecha}/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<?> getPedidosPorFechaAndEmprendimiento(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                                 @PathVariable Long idEmprendimiento) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<PedidoDTO> pedido = pedidoService.getAllPedidosByFechaAndEmprendimiento(fecha, idEmprendimiento, autenticado);

        if (!pedido.isEmpty()) {
            return ResponseEntity.ok(pedido);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron pedidos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Métodos de respuesta genéricos, pueden ser utilizados para simplificar el manejo de respuestas
    // Se podrian almacenar en una clase utils para hacerlo reutilizable en otros controladores
//    private ResponseEntity<?> respuestaOk(Object body) {
//        return ResponseEntity.ok(body);
//    }
//
//    private ResponseEntity<?> respuestaNoEncontrado(String mensaje) {
//        Map<String, String> response = new HashMap<>();
//        response.put("message", mensaje);
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//    }

    // Ejemplo de como usarlo con un Optional (falta testear)
//    @GetMapping("/{id}")
//    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Long id) {
//        return pedidoService.getPedidoById(id)
//                .map(this::respuestaOk)
//                .orElseGet(() -> respuestaNoEncontrado("Pedido no encontrado"));
//    }

    // Ejemplo de como usarlo con una lista (falta testear)
//    @GetMapping
//    public ResponseEntity<?> obtenerPedidos() {
//        List<PedidoDTO> pedidos = pedidoService.getAllPedidos();
//        return pedidos.isEmpty()
//                ? respuestaNoEncontrado("No se encontraron pedidos")
//                : respuestaOk(pedidos);
//    }

}