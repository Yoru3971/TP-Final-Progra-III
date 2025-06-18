package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;
import com.viandasApp.api.Vianda.service.ViandaService;
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
@RequestMapping("/api/dueno/viandas")
@RequiredArgsConstructor
public class ViandaDuenoController {

    private final ViandaService viandasService;

    //--------------------------Create--------------------------//
    @PostMapping
    public ResponseEntity<?> createVianda(@Valid @RequestBody ViandaCreateDTO dto) {

        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        ViandaDTO viandaCreada = viandasService.createVianda(dto, autenticado);

        Map<String, Object> response = new HashMap<>();
        response.put("Vianda creada correctamente:", viandaCreada);
        return ResponseEntity.ok(response);
    }

    //--------------------------Read--------------------------//
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<ViandaDTO>> getViandasByEmprendimiento(@Valid @ModelAttribute FiltroViandaDTO filtro, @PathVariable Long idEmprendimiento) {
        Usuario usuarioLogueado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ViandaDTO> resultados = viandasService.getViandasByEmprendimiento(filtro, idEmprendimiento, usuarioLogueado);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<ViandaDTO> vianda = viandasService.findViandaById(id, usuario);
        return ResponseEntity.ok(vianda);
    }

    //--------------------------Update--------------------------//
    @PutMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> updateVianda(@PathVariable Long id, @Valid @RequestBody ViandaUpdateDTO dto) {

        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<ViandaDTO> viandaActualizado = viandasService.updateVianda(id, dto, usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("Vianda actualizada correctamente:", viandaActualizado);
        return ResponseEntity.ok(response);
    }

    //--------------------------Delete--------------------------//
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