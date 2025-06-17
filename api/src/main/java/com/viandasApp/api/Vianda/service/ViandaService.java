package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface ViandaService {
    ViandaDTO createVianda(ViandaCreateDTO viandaDto, Usuario usuario);
    Optional<ViandaDTO> updateVianda(Long id, ViandaUpdateDTO viandaDto, Usuario usuario);
    boolean deleteVianda(Long id, Usuario usuario);

    Optional<ViandaDTO> findViandaById(Long id, Usuario usuario);
    List<ViandaDTO> getViandasByEmprendimiento(FiltroViandaDTO filtroViandaDTO, Long idEmprendimiento, Usuario usuario);
    List<ViandaDTO> getViandasDisponiblesByEmprendimiento(FiltroViandaDTO filtroViandaDTO, Long idEmprendimiento);
}
