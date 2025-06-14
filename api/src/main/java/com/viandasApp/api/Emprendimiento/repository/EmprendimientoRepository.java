package com.viandasApp.api.Emprendimiento.repository;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprendimientoRepository extends JpaRepository<Emprendimiento, Long> {

    List<Emprendimiento> findByNombreEmprendimientoContaining(String nombreEmprendimiento);
    List<Emprendimiento> findByCiudad(String ciudad);
    List<Emprendimiento> findByUsuarioId(Long id);

}
