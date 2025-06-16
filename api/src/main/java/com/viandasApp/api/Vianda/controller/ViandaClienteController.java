package com.viandasApp.api.Vianda.controller;

import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.service.ViandaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cliente/viandas")
public class ViandaClienteController {
    private final ViandaService viandasService;

    public ViandaClienteController(ViandaService viandasService) {
        this.viandasService = viandasService;
    }

    @GetMapping
    public ResponseEntity<List<ViandaDTO>> getAllViandas() {
        return ResponseEntity.ok(viandasService.getAllViandas());
    }

    @GetMapping("/id-emprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<ViandaDTO>> getViandasDisponiblesByEmprendimiento(
            @Valid @RequestBody FiltroViandaDTO filtro,
            @PathVariable Long idEmprendimiento) {
        List<ViandaDTO> resultados = viandasService.getViandasDisponiblesByEmprendimiento(filtro, idEmprendimiento);
        return ResponseEntity.ok(resultados);
    }

    // ---------------------------------------------------------------------------------------

    @GetMapping("/id/{id}")
    public ResponseEntity<ViandaDTO> findById(@PathVariable Long id) {
        return viandasService.findViandaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
