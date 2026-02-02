package com.viandasApp.api.Reclamo.controller;

import com.viandasApp.api.Reclamo.dto.ReclamoRequestDTO;
import com.viandasApp.api.Reclamo.service.ReclamoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Reclamos - Public")
@RequestMapping("/api/public/reclamos")
@RequiredArgsConstructor
public class ReclamoPublicController {

    private final ReclamoService reclamoService;

    @PostMapping
    public ResponseEntity<?> crearReclamo(@Valid @RequestBody ReclamoRequestDTO dto) {
        reclamoService.crearReclamo(dto);
        return ResponseEntity.ok("Reclamo enviado exitosamente. Revisá tu bandeja de entrada.");
    }
}

