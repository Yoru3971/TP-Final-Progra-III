package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.repository.EmprendimientoRepository;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;
import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.repository.ViandaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ViandaServiceImpl implements ViandaService {
    private final ViandaRepository repository;
    private final EmprendimientoRepository emprendimientoRepository;

    public ViandaServiceImpl(ViandaRepository repository, EmprendimientoRepository emprendimientoRepository) {
        this.repository = repository;
        this.emprendimientoRepository = emprendimientoRepository;
    }

    @Override
    public ViandaDTO createVianda(ViandaCreateDTO dto) {
        final Vianda vianda = DTOtoEntity(dto);
        final Vianda nuevaVianda = repository.save(vianda);
        return new ViandaDTO(nuevaVianda);
    }


    @Override
    public Optional<ViandaDTO> findViandaById(Long id) {
        return repository.findById(id).map(ViandaDTO::new);
    }

    @Override
    public Optional<ViandaDTO> updateVianda(Long id, ViandaUpdateDTO dto) {
        return repository.findById(id).map(existing -> {
            if (dto.getNombreVianda() != null) existing.setNombreVianda(dto.getNombreVianda());
            if (dto.getCategoria() != null) existing.setCategoria(dto.getCategoria());
            if (dto.getDescripcion() != null) existing.setDescripcion(dto.getDescripcion());
            if (dto.getPrecio() != null) existing.setPrecio(dto.getPrecio());
            if (dto.getEsVegano() != null) existing.setEsVegano(dto.getEsVegano());
            if (dto.getEsVegetariano() != null) existing.setEsVegetariano(dto.getEsVegetariano());
            if (dto.getEsSinTacc() != null) existing.setEsSinTacc(dto.getEsSinTacc());

            return new ViandaDTO(repository.save(existing));
        });
    }

    @Override
    public boolean deleteVianda(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    //  ---------   listado

    @Override
    public List<ViandaDTO> getAllViandas() {
        return repository.findAll().stream()
                .map(ViandaDTO::new)
                .toList();
    }

    @Override
    public List<ViandaDTO> getViandasByPrecio(Double min, Double max) {
        return repository.findByPrecioBetween(min, max)
                .stream()
                .map(ViandaDTO::new)
                .toList();
    }

    @Override
    public List<ViandaDTO> getViandasByNombre(String nombre) {
        return repository.findByNombreViandaContainingIgnoreCase(nombre)
                .stream()
                .map(ViandaDTO::new)
                .toList();
    }

    @Override
    public List<ViandaDTO> getViandasByEsSinTaccTrue() {
        return repository.findByEsSinTaccTrue()
                .stream()
                .map(ViandaDTO::new)
                .toList();
    }

    @Override
    public List<ViandaDTO> getViandasByEsVegetarianoTrue() {
        return repository.findByEsVegetarianoTrue()
                .stream()
                .map(ViandaDTO::new)
                .toList();
    }

    @Override
    public List<ViandaDTO> getViandasByEsVeganoTrue() {
        return repository.findByEsVeganoTrue()
                .stream()
                .map(ViandaDTO::new)
                .toList();
    }

    @Override
    public List<ViandaDTO> getViandasByEmprendimientoId(Long id) {
        return repository.findByEmprendimientoId(id)
                .stream()
                .map(ViandaDTO::new)
                .toList();
    }

    @Override
    public List<ViandaDTO> getViandasByCategoria(CategoriaVianda categoriaVianda) {
        return repository.findByCategoria(categoriaVianda)
                .stream()
                .map(ViandaDTO::new)
                .toList();
    }

    // --------- mapeo

    private Vianda DTOtoEntity(ViandaCreateDTO viandaDTO) {
        Optional<Emprendimiento> emprendimiento = emprendimientoRepository.findById(viandaDTO.getEmprendimientoId());

        return emprendimiento.map(value -> new Vianda(
                viandaDTO.getNombreVianda(),
                viandaDTO.getCategoria(),
                viandaDTO.getDescripcion(),
                viandaDTO.getPrecio(),
                viandaDTO.getEsVegano(),
                viandaDTO.getEsVegetariano(),
                viandaDTO.getEsSinTacc(),
                value
        )).orElseThrow(NullPointerException::new);

        // todo: Revisar la excepcion esa si esta bien o modificarla
    }
}