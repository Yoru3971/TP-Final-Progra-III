package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dueno/emprendimientos")
public class EmprendimientoDuenoController {

    private final EmprendimientoService emprendimientoService;

    public EmprendimientoDuenoController(EmprendimientoService emprendimientoService) {

        this.emprendimientoService = emprendimientoService;
    }

    @PostMapping
    public ResponseEntity<?> createEmprendimiento(
            @Valid @RequestBody CreateEmprendimientoDTO createEmprendimientoDTO,
            BindingResult result,
            Authentication authentication) {

        Usuario usuario = (Usuario) authentication.getPrincipal();
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        try {
            EmprendimientoDTO emprendimientoGuardado = emprendimientoService.createEmprendimiento(createEmprendimientoDTO, usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(emprendimientoGuardado);

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmprendimiento(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmprendimientoDTO updateEmprendimientoDTO,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        try {

            Optional<EmprendimientoDTO> emprendimientoActualizado = emprendimientoService.updateEmprendimiento(id, updateEmprendimientoDTO, usuario);

            if (emprendimientoActualizado.isEmpty()) {
                response.put("message", "El emprendimiento no se pudo actualizar.");
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(response);
            } else {
                response.put("message", "Emprendimiento actualizado correctamente.");
                response.put("emprendimiento", emprendimientoActualizado.get());
                return ResponseEntity.ok(response);
            }


        } catch (EntityNotFoundException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEmprendimiento(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        Map<String, String> response = new HashMap<>();
        boolean eliminado = emprendimientoService.deleteEmprendimiento(id, usuario);

        if (eliminado) {
            response.put("message", "Emprendimiento eliminado correctamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Emprendimiento no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

    @GetMapping("/id/{id}")
    public ResponseEntity<EmprendimientoDTO> getEmprendimientoById(
            @PathVariable Long id,
            Authentication authentication) {

        Usuario usuario = (Usuario) authentication.getPrincipal();
        Optional<EmprendimientoDTO> emprendimiento = emprendimientoService.getEmprendimientoById(id, usuario);

        return emprendimiento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/id-usuario/{idUsuario}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByUsuario(
            @PathVariable Long idUsuario,
            Authentication authentication) {

        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByUsuarioId(idUsuario, usuario);

        if (emprendimientos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(emprendimientos);
    }

}
