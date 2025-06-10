package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface ViandaService {
    ViandaDTO createVianda(ViandaCreateDTO viandaDto);
    Optional<ViandaDTO> findViandaById(Long id);
    Optional<ViandaDTO> updateVianda(Long id, ViandaUpdateDTO viandaDto);
    boolean deleteVianda(Long id);

    List<ViandaDTO> getAllViandas();
    List<ViandaDTO> getViandasByEmprendimientoId(Long id);
    List<ViandaDTO> getViandasByPrecio(Double min, Double max);
    List<ViandaDTO> getViandasByNombre(String nombre);
    List<ViandaDTO> getViandasByEsSinTaccTrue();
    List<ViandaDTO> getViandasByEsVegetarianoTrue();
    List<ViandaDTO> getViandasByEsVeganoTrue();
}
