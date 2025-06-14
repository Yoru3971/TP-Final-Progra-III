package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface ViandaService {
    ViandaDTO create(ViandaCreateDTO viandaDto);
    List<ViandaDTO> read();
    Optional<ViandaDTO> findById(Long id);
    Optional<ViandaDTO> update(Long id, ViandaUpdateDTO viandaDto);
    boolean delete(Long id);
}
