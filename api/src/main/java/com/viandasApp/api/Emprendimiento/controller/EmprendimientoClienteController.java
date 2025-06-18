package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cliente/emprendimientos")
@RequiredArgsConstructor
public class EmprendimientoClienteController {

    private final EmprendimientoService emprendimientoService;

    //--------------------------Read--------------------------//
    @GetMapping
    public ResponseEntity<List<EmprendimientoDTO>> getAllEmprendimientos(){
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getAllEmprendimientos();
        return ResponseEntity.ok(emprendimientos);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getEmprendimientoById (@PathVariable Long id, Usuario usuario){
        Optional<EmprendimientoDTO> emprendimiento = emprendimientoService.getEmprendimientoById(id, usuario);
        return ResponseEntity.ok(emprendimiento);
    }

    @GetMapping("/nombre/{nombreEmprendimiento}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByNombre(@PathVariable String nombreEmprendimiento){
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByNombre(nombreEmprendimiento);
        return ResponseEntity.ok(emprendimientos);
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByCiudad(@PathVariable String ciudad){
        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByCiudad(ciudad);
        return ResponseEntity.ok(emprendimientos);
    }
}
