package com.viandasApp.api.Emprendimiento.repository;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.User.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprendimientoRepository extends JpaRepository<Emprendimiento, Long> {

    List<Emprendimiento> findByNombreEmprendimientoContaining(String nombreEmprendimiento);
    List<Emprendimiento> findByCiudad(String ciudad);
    //  List<Emprendimiento> findByUsuario(User usuario);
    List<Emprendimiento> findByUsuarioId(Long id);

}
