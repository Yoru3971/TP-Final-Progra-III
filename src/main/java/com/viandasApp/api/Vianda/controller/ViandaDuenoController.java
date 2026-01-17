package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;
import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.service.ViandaService;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Viandas - Dueño")
@RequestMapping("/api/dueno/viandas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DUENO')")
public class ViandaDuenoController {

    private final ViandaService viandasService;
    private final PagedResourcesAssembler<ViandaDTO> pagedResourcesAssembler;

    //--------------------------Create--------------------------//
     @Operation(
            summary = "Crear una nueva vianda",
            description = "Crea una nueva vianda asociada al emprendimiento del dueño autenticado",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vianda creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(consumes = "multipart/form-data")
     /// Usamos @ModelAttribute en lugar de @RequestBody porque ahora tenemos un archivo junto con datos.
    public ResponseEntity<?> createVianda(@Valid @ModelAttribute ViandaCreateDTO dto) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        ViandaDTO viandaCreada = viandasService.createVianda(dto, autenticado);

         viandaCreada.add(linkTo(methodOn(ViandaDuenoController.class).getById(viandaCreada.getId())).withSelfRel());

        Map<String, Object> response = new HashMap<>();
        response.put("Vianda creada correctamente:", viandaCreada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    //--------------------------Read--------------------------//
     @Operation(
            summary = "Obtener viandas por emprendimiento",
            description = "Obtiene una lista paginada de viandas filtradas por emprendimiento y otros criterios",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Viandas obtenidas correctamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
                    @ApiResponse(responseCode = "404", description = "Emprendimiento no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
     public ResponseEntity<PagedModel<EntityModel<ViandaDTO>>> getViandasByEmprendimiento(
             @Valid @ModelAttribute FiltroViandaDTO filtro,
             @PathVariable Long idEmprendimiento,
             @PageableDefault(size = 10) Pageable pageable) {

         Usuario usuarioLogueado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         Page<ViandaDTO> page = viandasService.getViandasByEmprendimiento(filtro, idEmprendimiento, usuarioLogueado, false, pageable);

         PagedModel<EntityModel<ViandaDTO>> pagedModel = pagedResourcesAssembler.toModel(page, vianda -> {
             vianda.add(linkTo(methodOn(ViandaDuenoController.class).getById(vianda.getId())).withSelfRel());
             vianda.add(linkTo(methodOn(ViandaDuenoController.class).updateVianda(vianda.getId(), null)).withRel("update"));
             vianda.add(linkTo(methodOn(ViandaDuenoController.class).deleteVianda(vianda.getId())).withRel("delete"));
             return EntityModel.of(vianda);
         });

         return ResponseEntity.ok(pagedModel);
     }

    @Operation(
            summary = "Obtener vianda por ID",
            description = "Obtiene una vianda específica por su ID",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vianda encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Vianda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<ViandaDTO> getById(@PathVariable Long id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ViandaDTO vianda = viandasService.findViandaById(id, usuario).get();

        vianda.add(linkTo(methodOn(ViandaDuenoController.class).getById(id)).withSelfRel());
        vianda.add(linkTo(methodOn(ViandaDuenoController.class).updateVianda(id, null)).withRel("update"));
        vianda.add(linkTo(methodOn(ViandaDuenoController.class).deleteVianda(id)).withRel("delete"));

        return ResponseEntity.ok(vianda);
    }

    @Operation(
            summary = "Obtener categorías por emprendimiento (Dueño)",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @GetMapping("/categorias/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<CategoriaVianda>> getCategoriasByEmprendimiento(@PathVariable Long idEmprendimiento) {
        Usuario usuarioLogueado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CategoriaVianda> categorias = viandasService.getCategoriasByEmprendimiento(idEmprendimiento, usuarioLogueado);
        return ResponseEntity.ok(categorias);
    }

    //--------------------------Update--------------------------//
    
   @Operation(
            summary = "Actualizar una vianda",
            description = "Actualiza los detalles de una vianda existente",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vianda actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Vianda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> updateVianda(@PathVariable Long id, @Valid @RequestBody ViandaUpdateDTO dto) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<ViandaDTO> viandaActualizado = viandasService.updateVianda(id, dto, usuario);

       if(viandaActualizado.isPresent()){
           viandaActualizado.get().add(linkTo(methodOn(ViandaDuenoController.class).getById(id)).withSelfRel());
       }

        Map<String, Object> response = new HashMap<>();
        response.put("Vianda actualizada correctamente:", viandaActualizado);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar la imagen de una vianda",
            description = "Actualiza la imagen de una vianda existente",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vianda actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Vianda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/id/{id}/imagen")
    public ResponseEntity<ViandaDTO> updateImageVianda(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image
    ) {
         Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                 .getAuthentication().getPrincipal();

         ViandaDTO viandaActualizada = viandasService.updateImagenVianda(id, image, usuario);
        viandaActualizada.add(linkTo(methodOn(ViandaDuenoController.class).getById(id)).withSelfRel());
         return ResponseEntity.ok(viandaActualizada);
    }

    //--------------------------Delete--------------------------//
    @Operation(
            summary = "Eliminar una vianda",
            description = "Elimina una vianda existente por su ID",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vianda eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado, se requiere login"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, no tenés el rol necesario"),
            @ApiResponse(responseCode = "404", description = "Vianda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Map<String, String>> deleteVianda(@PathVariable Long id) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Map<String, String> response = new HashMap<>();

        viandasService.deleteVianda(id, usuario);
        response.put("message", "Vianda eliminada correctamente");
        return ResponseEntity.ok(response);
    }
}