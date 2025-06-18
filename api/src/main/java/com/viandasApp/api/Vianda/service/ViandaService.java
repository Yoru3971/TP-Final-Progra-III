package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;
import com.viandasApp.api.Vianda.model.Vianda;

import java.util.List;
import java.util.Optional;

public interface ViandaService {
    //--------------------------Create--------------------------//
    ViandaDTO createVianda(ViandaCreateDTO viandaDto, Usuario usuario);

    //--------------------------Read--------------------------//
    List<ViandaDTO> getViandasByEmprendimiento(FiltroViandaDTO filtroViandaDTO, Long idEmprendimiento, Usuario usuario);
    List<ViandaDTO> getViandasDisponiblesByEmprendimiento(FiltroViandaDTO filtroViandaDTO, Long idEmprendimiento);
    Optional<ViandaDTO> findViandaById(Long id, Usuario usuario);

    //--------------------------Update--------------------------//
    Optional<ViandaDTO> updateVianda(Long id, ViandaUpdateDTO viandaDto, Usuario usuario);

    //--------------------------Delete--------------------------//
    boolean deleteVianda(Long id, Usuario usuario);

    //--------------------------Otros--------------------------//
    Optional<Vianda> findEntityViandaById(Long id);
}
