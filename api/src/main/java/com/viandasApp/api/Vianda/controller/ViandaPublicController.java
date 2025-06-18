package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.service.ViandaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/viandas")
@RequiredArgsConstructor
public class ViandaPublicController {

    private final ViandaService viandasService;

    //--------------------------Read--------------------------//
    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<ViandaDTO>> getViandasDisponiblesByEmprendimiento(@Valid @ModelAttribute FiltroViandaDTO filtro, @PathVariable Long idEmprendimiento) {
        List<ViandaDTO> resultados = viandasService.getViandasDisponiblesByEmprendimiento(filtro, idEmprendimiento);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<ViandaDTO> vianda = viandasService.findViandaById(id, usuario);
        return ResponseEntity.ok(vianda);
    }
}