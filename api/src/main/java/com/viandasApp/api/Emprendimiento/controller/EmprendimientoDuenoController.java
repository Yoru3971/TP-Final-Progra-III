package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Usuario.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dueno/emprendimientos")
@RequiredArgsConstructor
public class EmprendimientoDuenoController {

    private final EmprendimientoService emprendimientoService;

    //--------------------------Create--------------------------//
    @PostMapping
    public ResponseEntity<?> createEmprendimiento(@Valid @RequestBody CreateEmprendimientoDTO createEmprendimientoDTO) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        EmprendimientoDTO emprendimientoGuardado = emprendimientoService.createEmprendimiento(createEmprendimientoDTO, usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("Emprendimiento creado correctamente:", emprendimientoGuardado);
        return ResponseEntity.ok(response);
    }

    //--------------------------Read--------------------------//
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getEmprendimientoById(@PathVariable Long id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<EmprendimientoDTO> emprendimiento = emprendimientoService.getEmprendimientoById(id, usuario);
        return ResponseEntity.ok(emprendimiento);
    }

    @GetMapping
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosPropios() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByUsuarioId(usuario.getId(), usuario);
        return ResponseEntity.ok(emprendimientos);
    }

    //--------------------------Update--------------------------//
    @PutMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> updateEmprendimiento(@PathVariable Long id, @Valid @RequestBody UpdateEmprendimientoDTO updateEmprendimientoDTO) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<EmprendimientoDTO> emprendimientoActualizado = emprendimientoService.updateEmprendimiento(id, updateEmprendimientoDTO, usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("Emprendimiento actualizado correctamente:", emprendimientoActualizado);
        return ResponseEntity.ok(response);
    }

    //--------------------------Delete--------------------------//
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
