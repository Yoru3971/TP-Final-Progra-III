package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.repository.ViandaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ViandaServiceImpl implements ViandaService {
    private final ViandaRepository repository;

    public ViandaServiceImpl(ViandaRepository repository) {
        this.repository = repository;
    }

    @Override
    public ViandaDTO create(ViandaCreateDTO dto) {
        final Vianda vianda = DTOtoEntity(dto);
        final Vianda nuevaVianda = repository.save(vianda);
        return entityToDTO(nuevaVianda);
    }

    @Override
    public List<ViandaDTO> read() {
        return repository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ViandaDTO> findById(Long id) {
        return repository.findById(id).map(this::entityToDTO);
    }

    @Override
    public Optional<ViandaDTO> update(Long id, ViandaUpdateDTO dto) {
        return repository.findById(id).map(existing -> {
            if (dto.getNombreVianda() != null) existing.setNombreVianda(dto.getNombreVianda());
            if (dto.getCategoria() != null) existing.setCategoria(dto.getCategoria());
            if (dto.getDescripcion() != null) existing.setDescripcion(dto.getDescripcion());
            if (dto.getPrecio() != null) existing.setPrecio(dto.getPrecio());
            if (dto.getEsVegano() != null) existing.setEsVegano(dto.getEsVegano());
            if (dto.getEsVegetariano() != null) existing.setEsVegetariano(dto.getEsVegetariano());
            if (dto.getEsSinTacc() != null) existing.setEsSinTacc(dto.getEsSinTacc());

            return entityToDTO(repository.save(existing));
        });
    }

    @Override
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private ViandaDTO entityToDTO(Vianda vianda) {
        return new ViandaDTO(
                vianda.getId(),
                vianda.getNombreVianda(),
                vianda.getCategoria(),
                vianda.getDescripcion(),
                vianda.getPrecio(),
                vianda.getEsVegano(),
                vianda.getEsVegetariano(),
                vianda.getEsSinTacc(),
                vianda.getEmprendimiento()
        );
    }

    private Vianda DTOtoEntity(ViandaCreateDTO viandaDTO) {
        return new Vianda(
                viandaDTO.getNombreVianda(),
                viandaDTO.getCategoria(),
                viandaDTO.getDescripcion(),
                viandaDTO.getPrecio(),
                viandaDTO.getEsVegano(),
                viandaDTO.getEsVegetariano(),
                viandaDTO.getEsSinTacc(),
                viandaDTO.getEmprendimiento()
        );
    }
}