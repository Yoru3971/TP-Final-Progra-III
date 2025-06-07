package com.viandasApp.api.Emprendimiento.repository;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprendimientoRepository extends JpaRepository<Emprendimiento, Long> {

    List<Emprendimiento> findByContieneNombreEmprendimiento(String nombreEmprendimiento);
    List<Emprendimiento> findByCiudad(String ciudad);
    List<Emprendimiento> findByDireccion(String direccion);
    //  Revisar si conviene por Id o por usuario
    List<Emprendimiento> findByDuenioId(String id);

}
