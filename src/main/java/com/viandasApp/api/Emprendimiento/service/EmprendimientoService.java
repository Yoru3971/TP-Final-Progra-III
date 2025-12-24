package com.viandasApp.api.Emprendimiento.service;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface EmprendimientoService {
    //--------------------------Create--------------------------//
    EmprendimientoDTO createEmprendimiento(CreateEmprendimientoDTO createEmprendimientoDTO, Usuario usuario);

    //--------------------------Read--------------------------//
    List<EmprendimientoDTO> getAllEmprendimientos();
    List<EmprendimientoDTO> getAllEmprendimientosDisponibles();
    Optional<EmprendimientoDTO> getEmprendimientoById(Long id, Usuario usuario);
    Optional<EmprendimientoDTO> getEmprendimientoByIdPublic(Long id);
    List<EmprendimientoDTO> getEmprendimientosByNombre(String nombreEmprendimiento);
    List<EmprendimientoDTO> getEmprendimientosDisponiblesByNombre(String nombreEmprendimiento);
    List<EmprendimientoDTO> getEmprendimientosByCiudad(String ciudad);
    List<EmprendimientoDTO> getEmprendimientosDisponiblesByCiudad(String ciudad);
    List<EmprendimientoDTO> getEmprendimientosByUsuarioId(Long id, Usuario usuario);

    //--------------------------Update--------------------------//
    Optional<EmprendimientoDTO> updateEmprendimiento(Long id, UpdateEmprendimientoDTO updateEmprendimientoDTO, Usuario usuario);
    EmprendimientoDTO updateImagenEmprendimiento(Long id, MultipartFile image, Usuario usuarioLogueado);

    //--------------------------Delete--------------------------//
    boolean deleteEmprendimiento(Long id, Usuario usuario);

    //--------------------------Otros--------------------------//
    Optional<Emprendimiento> findEntityById(Long id);
}
