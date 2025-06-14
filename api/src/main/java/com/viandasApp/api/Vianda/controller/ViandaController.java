package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;
import com.viandasApp.api.Vianda.service.ViandaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/viandas")
public class ViandaController {
    private final ViandaService viandasService;

    public ViandaController(ViandaService viandasService) {
        this.viandasService = viandasService;
    }

    @PostMapping
    public ResponseEntity<ViandaDTO> createVianda(@Valid @RequestBody ViandaCreateDTO dto) {
        return new ResponseEntity<>(viandasService.createVianda(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ViandaDTO>> getAllViandas() {
        return ResponseEntity.ok(viandasService.getAllViandas());
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<ViandaDTO>> filtrarViandas(@Valid @RequestBody FiltroViandaDTO filtro) {
        List<ViandaDTO> resultados = viandasService.filtrarViandas(filtro);
        return ResponseEntity.ok(resultados);
    }

    // ---------------------------------------------------------------------------------------

    @GetMapping("/id/{id}")
    public ResponseEntity<ViandaDTO> findById(@PathVariable Long id) {
        return viandasService.findViandaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<ViandaDTO> updateVianda(
            @PathVariable Long id,
            @Valid @RequestBody ViandaUpdateDTO dto
    ) {
        return viandasService.updateVianda(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteVianda(@PathVariable Long id) {
        return viandasService.deleteVianda(id)
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : ResponseEntity.notFound().build();
    }
}