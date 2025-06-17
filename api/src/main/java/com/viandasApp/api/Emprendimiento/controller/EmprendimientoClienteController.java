package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cliente/emprendimientos")
public class EmprendimientoClienteController {

    private final EmprendimientoService emprendimientoService;

    public EmprendimientoClienteController(EmprendimientoService emprendimientoService) {

        this.emprendimientoService = emprendimientoService;
    }


    @GetMapping
    public ResponseEntity<List<EmprendimientoDTO>> getAllEmprendimientos(){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getAllEmprendimientos();

        if ( emprendimientos.isEmpty() ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(emprendimientos);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<EmprendimientoDTO> getEmprendimientoById (@PathVariable Long id){

        Optional<EmprendimientoDTO> emprendimiento = emprendimientoService.getEmprendimientoById(id);

        return emprendimiento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombreEmp}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByNombre(@PathVariable String nombreEmp){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByNombre(nombreEmp);

        if ( emprendimientos.isEmpty() ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(emprendimientos);
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByCiudad(@PathVariable String ciudad){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByCiudad(ciudad);

        if ( emprendimientos.isEmpty() ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(emprendimientos);
    }

    @GetMapping("/id-usuario/{idUsuario}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByUsuario(@PathVariable Long idUsuario){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByUsuarioId(idUsuario);

        if ( emprendimientos.isEmpty() ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(emprendimientos);
    }
}
