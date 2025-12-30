package com.viandasApp.api.Emprendimiento.repository;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmprendimientoRepository extends JpaRepository<Emprendimiento, Long> {

    List<Emprendimiento> findByNombreEmprendimientoContaining(String nombreEmprendimiento);
    List<Emprendimiento> findByNombreEmprendimientoContainingAndEstaDisponibleTrue(String nombreEmprendimiento);
    List<Emprendimiento> findByCiudad(String ciudad);
    List<Emprendimiento> findByCiudadAndEstaDisponibleTrue(String ciudad);
    List<Emprendimiento> findByEstaDisponibleTrue();
    List<Emprendimiento> findByUsuarioId(Long id);
    List<Emprendimiento> findByTelefono(String telefono);

    //  Paginación para Public/Cliente
    Page<Emprendimiento> findByEstaDisponibleTrue(Pageable pageable);

    //  Paginación para Dueño
    Page<Emprendimiento> findByUsuarioId(Long id, Pageable pageable);
}
