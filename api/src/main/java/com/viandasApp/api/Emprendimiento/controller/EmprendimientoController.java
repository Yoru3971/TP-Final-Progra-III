package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.User.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/emprendimientos")
public class EmprendimientoController {

    private final EmprendimientoService emprendimientoService;

    public EmprendimientoController(EmprendimientoService emprendimientoService) {

        this.emprendimientoService = emprendimientoService;
    }


    @GetMapping
    public ResponseEntity<List<EmprendimientoDTO>> getAllEmprendimientos(){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getAllEmprendimientos();

        return ResponseEntity.ok(emprendimientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprendimientoDTO> getEmprendimientoById (@PathVariable Long id){

        Optional<EmprendimientoDTO> emprendimiento = emprendimientoService.getEmprendimientoById(id);

        return emprendimiento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByNombre(String nombreEmp){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByNombre(nombreEmp);

        return ResponseEntity.ok(emprendimientos);
    }

    @GetMapping("/{ciudad}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByCiudad(String ciudad){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByCiudad(ciudad);

        return ResponseEntity.ok(emprendimientos);
    }

    @GetMapping("/{usuario}")
    public ResponseEntity<List<EmprendimientoDTO>> getEmprendimientosByUsuario(User usuario){

        List<EmprendimientoDTO> emprendimientos = emprendimientoService.getEmprendimientosByUsuario(usuario);

        return ResponseEntity.ok(emprendimientos);
    }

    @PostMapping
    public ResponseEntity<EmprendimientoDTO> createEmprendimiento(@Valid @RequestBody CreateEmprendimientoDTO createEmprendimientoDTO){

        EmprendimientoDTO emprendimientoGuardado = emprendimientoService.createEmprendimiento(createEmprendimientoDTO);

        return new ResponseEntity<>(emprendimientoGuardado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmprendimientoDTO> updateEmprendimiento(@PathVariable Long id, @Valid @RequestBody UpdateEmprendimientoDTO updateEmprendimientoDTO){

        Optional<EmprendimientoDTO> emprendimientoActualizado = emprendimientoService.updateEmprendimiento(id, updateEmprendimientoDTO);

        return emprendimientoActualizado.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmprendimiento(@PathVariable Long id){

        boolean eliminado = emprendimientoService.deleteEmprendimiento(id);

        if ( eliminado ){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

}
