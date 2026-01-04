package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Usuario.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@Tag(name = "Emprendimientos - Dueño")
@RequestMapping("/api/dueno/emprendimientos")
@RequiredArgsConstructor
public class EmprendimientoDuenoController {
    private final EmprendimientoService emprendimientoService;
    private final PagedResourcesAssembler<EmprendimientoDTO> pagedResourcesAssembler;
   
    //--------------------------Create--------------------------//
   @Operation(
            summary = "Crear un nuevo emprendimiento",
            description = "Permite a un dueño crear un nuevo emprendimiento asociado a su usuario.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Emprendimiento creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Entidad no encontrada, por ejemplo, usuario no existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(consumes = "multipart/form-data")
   /// Usamos @ModelAttribute en lugar de @RequestBody porque ahora tenemos un archivo junto con datos.
    public ResponseEntity<?> createEmprendimiento(@Valid @ModelAttribute CreateEmprendimientoDTO createEmprendimientoDTO) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        EmprendimientoDTO emprendimientoGuardado = emprendimientoService.createEmprendimiento(createEmprendimientoDTO, usuario);

       emprendimientoGuardado.add(linkTo(methodOn(EmprendimientoDuenoController.class).getEmprendimientoById(emprendimientoGuardado.getId())).withSelfRel());

        Map<String, Object> response = new HashMap<>();
        response.put("Emprendimiento creado correctamente:", emprendimientoGuardado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //--------------------------Read--------------------------//
    @Operation(
            summary = "Obtener emprendimiento por ID",
            description = "Permite a un dueño obtener un emprendimiento específico por su ID.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<EmprendimientoDTO> getEmprendimientoById(@PathVariable Long id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmprendimientoDTO emprendimiento = emprendimientoService.getEmprendimientoById(id, usuario).get();

        emprendimiento.add(linkTo(methodOn(EmprendimientoDuenoController.class).getEmprendimientoById(id)).withSelfRel());
        emprendimiento.add(linkTo(methodOn(EmprendimientoDuenoController.class).updateEmprendimiento(id, null)).withRel("update"));
        emprendimiento.add(linkTo(methodOn(EmprendimientoDuenoController.class).deleteEmprendimiento(id)).withRel("delete"));

        return ResponseEntity.ok(emprendimiento);
    }
  
    @Operation(
            summary = "Obtener emprendimientos propios (con paginación y filtro opcional de ciudad)",
            description = "Permite a un dueño obtener una página de sus propios emprendimientos.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimientos encontrados"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "No se encontraron emprendimientos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<EmprendimientoDTO>>> getEmprendimientosPropios(
            @RequestParam(required = false) String ciudad,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Pasamos la ciudad (puede ser null) al servicio
        Page<EmprendimientoDTO> page = emprendimientoService.getEmprendimientosByUsuario(usuario.getId(), usuario, ciudad, pageable);

        PagedModel<EntityModel<EmprendimientoDTO>> pagedModel = pagedResourcesAssembler.toModel(page, e -> {
            e.add(linkTo(methodOn(EmprendimientoDuenoController.class).getEmprendimientoById(e.getId())).withSelfRel());
            e.add(linkTo(methodOn(EmprendimientoDuenoController.class).updateEmprendimiento(e.getId(), null)).withRel("update"));
            e.add(linkTo(methodOn(EmprendimientoDuenoController.class).deleteEmprendimiento(e.getId())).withRel("delete"));
            return EntityModel.of(e);
        });

        return ResponseEntity.ok(pagedModel);
    }

    //--------------------------Update--------------------------//
    @Operation(
            summary = "Actualizar un emprendimiento por ID",
            description = "Permite a un dueño actualizar la información de un emprendimiento específico por su ID.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> updateEmprendimiento(@PathVariable Long id, @Valid @RequestBody UpdateEmprendimientoDTO updateEmprendimientoDTO) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<EmprendimientoDTO> emprendimientoActualizado = emprendimientoService.updateEmprendimiento(id, updateEmprendimientoDTO, usuario);

        if (emprendimientoActualizado.isPresent()) {
            emprendimientoActualizado.get().add(linkTo(methodOn(EmprendimientoDuenoController.class).getEmprendimientoById(id)).withSelfRel());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("Emprendimiento actualizado correctamente:", emprendimientoActualizado);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar la imagen de un emprendimiento por ID",
            description = "Permite a un dueño actualizar la imagen de un emprendimiento específico por su ID.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/id/{id}/imagen")
    public ResponseEntity<EmprendimientoDTO> updateImagenEmprendimiento(
            @PathVariable Long id,
            @RequestParam("image")MultipartFile image
    ) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        EmprendimientoDTO emprendimientoActualizado = emprendimientoService.updateImagenEmprendimiento(id, image, usuario);

        emprendimientoActualizado.add(linkTo(methodOn(EmprendimientoDuenoController.class).getEmprendimientoById(id)).withSelfRel());

        return ResponseEntity.ok(emprendimientoActualizado);
    }
    
  //--------------------------Delete--------------------------//
     @Operation(
            summary = "Eliminar un emprendimiento por ID",
            description = "Permite a un dueño eliminar un emprendimiento específico por su ID.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprendimiento eliminado correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>> deleteEmprendimiento(@PathVariable Long id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Map<String, String> response = new HashMap<>();

        emprendimientoService.deleteEmprendimiento(id, usuario);
        response.put("message", "Emprendimiento eliminado correctamente");
        return ResponseEntity.ok(response);
    }
}
