package com.viandasApp.api.Emprendimiento.controller;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/emprendimientos")
public class EmprendimientoController {

    private final EmprendimientoService emprendimientoService;

    public EmprendimientoController(EmprendimientoService emprendimientoService) {

        this.emprendimientoService = emprendimientoService;
    }


    @PostMapping
    public ResponseEntity<?> createEmprendimiento(@Valid @RequestBody CreateEmprendimientoDTO createEmprendimientoDTO, BindingResult result){

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        try {

            EmprendimientoDTO emprendimientoGuardado = emprendimientoService.createEmprendimiento(createEmprendimientoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(emprendimientoGuardado);

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmprendimiento(@PathVariable Long id, @Valid @RequestBody UpdateEmprendimientoDTO updateEmprendimientoDTO){

        Map<String, Object> response = new HashMap<>();

        try {

            Optional<EmprendimientoDTO> emprendimientoActualizado = emprendimientoService.updateEmprendimiento(id, updateEmprendimientoDTO);

            if (emprendimientoActualizado.isEmpty()) {
                response.put("message", "El emprendimiento no se pudo actualizar.");
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(response);
            }else {
                response.put("message", "Emprendimiento actualizado correctamente.");
                response.put("emprendimiento", emprendimientoActualizado.get());
                return ResponseEntity.ok(response);
            }


        } catch (EntityNotFoundException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEmprendimiento(@PathVariable Long id){

        Map<String, String> response = new HashMap<>();
        boolean eliminado = emprendimientoService.deleteEmprendimiento(id);

        if ( eliminado ){
            response.put("message", "Emprendimiento eliminado correctamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Emprendimiento no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

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
