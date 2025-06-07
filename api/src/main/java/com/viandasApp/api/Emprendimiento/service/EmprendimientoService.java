package com.viandasApp.api.Emprendimiento.service;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.User.model.User;

import java.util.List;
import java.util.Optional;

public interface EmprendimientoService {

    List<EmprendimientoDTO> getAllEmprendimientos();
    Optional<EmprendimientoDTO> getEmprendimientoById(Long id);
    List<EmprendimientoDTO> getEmprendimientosByNombre(String nombreEmprendimiento);
    List<EmprendimientoDTO> getEmprendimientosByCiudad(String ciudad);
    List<EmprendimientoDTO> getEmprendimientosByUsuario(User usuario);
    EmprendimientoDTO createEmprendimiento(CreateEmprendimientoDTO createEmprendimientoDTO);
    Optional<EmprendimientoDTO> updateEmprendimiento(Long id, UpdateEmprendimientoDTO updateEmprendimientoDTO);
    boolean deleteEmprendimiento(Long id);

}
