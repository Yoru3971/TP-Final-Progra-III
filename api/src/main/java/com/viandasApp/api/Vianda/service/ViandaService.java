package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;
import com.viandasApp.api.Vianda.model.Vianda;

import java.util.List;
import java.util.Optional;

public interface ViandaService {
    ViandaDTO createVianda(ViandaCreateDTO viandaDto);
    Optional<ViandaDTO> findViandaById(Long id);

    Optional<Vianda> findEntityViandaById(Long id);

    Optional<ViandaDTO> updateVianda(Long id, ViandaUpdateDTO viandaDto);
    boolean deleteVianda(Long id);

    List<ViandaDTO> getAllViandas();
    List<ViandaDTO> filtrarViandas(FiltroViandaDTO filtroViandaDTO);
}
