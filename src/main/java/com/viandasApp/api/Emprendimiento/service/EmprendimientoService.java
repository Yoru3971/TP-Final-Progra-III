package com.viandasApp.api.Emprendimiento.service;

import com.viandasApp.api.Emprendimiento.dto.*;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface EmprendimientoService {
    //--------------------------Create--------------------------//
    EmprendimientoDTO createEmprendimiento(CreateEmprendimientoDTO createEmprendimientoDTO, Usuario usuario);

    //--------------------------Read (Paginación)--------------------------//
    Page<Emprendimiento> buscarEmprendimientos(Usuario usuario, String ciudad, String nombre, String nombreDueno, Boolean soloEliminados, Pageable pageable);
    Page<EmprendimientoDTO> getAllEmprendimientosDisponibles(Pageable pageable);
    Page<EmprendimientoDTO> getEmprendimientosDisponiblesByCiudad(String ciudad, Pageable pageable);
    Page<EmprendimientoDTO> getEmprendimientosByUsuario(Long idUsuario, Usuario usuario, String ciudad, Pageable pageable);
    Page<EmprendimientoAdminDTO> getAllEmprendimientosForAdmin(Pageable pageable);

    //--------------------------Read--------------------------//
    List<EmprendimientoDTO> getAllEmprendimientos();
    Optional<EmprendimientoDTO> getEmprendimientoById(Long id, Usuario usuario);
    Optional<EmprendimientoDTO> getEmprendimientoByIdPublic(Long id);
    List<EmprendimientoDTO> getEmprendimientosByNombre(String nombreEmprendimiento);
    List<EmprendimientoDTO> getEmprendimientosDisponiblesByNombre(String nombreEmprendimiento);
    List<EmprendimientoDTO> getEmprendimientosByCiudad(String ciudad);
    List<EmprendimientoDTO> getEmprendimientosDisponiblesByCiudad(String ciudad);

    //--------------------------Update--------------------------//
    Optional<EmprendimientoDTO> updateEmprendimiento(Long id, UpdateEmprendimientoDTO updateEmprendimientoDTO, Usuario usuario);
    EmprendimientoDTO updateImagenEmprendimiento(Long id, MultipartFile image, Usuario usuarioLogueado);

    //--------------------------Delete--------------------------//
    boolean deleteEmprendimiento(Long id, Usuario usuario, boolean forzar);

    //--------------------------Otros--------------------------//
    Optional<Emprendimiento> findEntityById(Long id);
}
