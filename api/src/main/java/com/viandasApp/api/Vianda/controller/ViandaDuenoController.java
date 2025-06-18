package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;
import com.viandasApp.api.Vianda.service.ViandaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dueno/viandas")
public class ViandaDuenoController {
    private final ViandaService viandasService;

    public ViandaDuenoController(ViandaService viandasService) {
        this.viandasService = viandasService;
    }

    @GetMapping("/idEmprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<ViandaDTO>> getViandasByEmprendimiento(
            @Valid @ModelAttribute FiltroViandaDTO filtro,
            @PathVariable Long idEmprendimiento) {

        Usuario usuarioLogueado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ViandaDTO> resultados = viandasService.getViandasByEmprendimiento(filtro, idEmprendimiento, usuarioLogueado);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ViandaDTO> findById(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return viandasService.findViandaById(id, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------------------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<ViandaDTO> createVianda(
            @Valid @RequestBody ViandaCreateDTO dto) {
        Usuario autenticado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return new ResponseEntity<>(viandasService.createVianda(dto, autenticado), HttpStatus.CREATED);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<ViandaDTO> updateVianda(
            @PathVariable Long id,
            @Valid @RequestBody ViandaUpdateDTO dto,
            Authentication authentication
    ) {

        Usuario usuario = (Usuario) authentication.getPrincipal();
        return viandasService.updateVianda(id, dto, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteVianda(
            @PathVariable Long id,
            Authentication authentication) {

        Usuario usuario = (Usuario) authentication.getPrincipal();
        return viandasService.deleteVianda(id, usuario)
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : ResponseEntity.notFound().build();
    }
}