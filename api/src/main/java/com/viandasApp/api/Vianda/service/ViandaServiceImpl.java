package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
import com.viandasApp.api.Vianda.dto.FiltroViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.dto.ViandaDTO;
import com.viandasApp.api.Vianda.dto.ViandaUpdateDTO;
import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.repository.ViandaRepository;
import com.viandasApp.api.Vianda.specification.ViandaSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ViandaServiceImpl implements ViandaService {
    private final ViandaRepository repository;
    private final EmprendimientoServiceImpl emprendimientoService;

    public ViandaServiceImpl(ViandaRepository repository, EmprendimientoServiceImpl emprendimientoService) {
        this.repository = repository;
        this.emprendimientoService = emprendimientoService;
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
            if (dto.getEstaDisponible() != null) existing.setEstaDisponible(dto.getEstaDisponible());

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
    public List<ViandaDTO> filtrarViandas(FiltroViandaDTO filtroViandaDTO) {
        Specification<Vianda> spec = ViandaSpecifications.estaDisponible();

        if (filtroViandaDTO.getEmprendimientoId() != null)
            spec = spec.and(ViandaSpecifications.perteneceAEmprendimiento(filtroViandaDTO.getEmprendimientoId()));

        if (filtroViandaDTO.getEsVegano() != null)
            spec = spec.and(ViandaSpecifications.esVegana(filtroViandaDTO.getEsVegano()));

        if (filtroViandaDTO.getEsVegetariano() != null)
            spec = spec.and(ViandaSpecifications.esVegetariana(filtroViandaDTO.getEsVegetariano()));

        if (filtroViandaDTO.getEsSinTacc() != null)
            spec = spec.and(ViandaSpecifications.esSinTacc(filtroViandaDTO.getEsSinTacc()));

        if (filtroViandaDTO.getCategoria() != null) {
            try {
                CategoriaVianda categoriaEnum = CategoriaVianda.fromDescripcion(filtroViandaDTO.getCategoria());
                spec = spec.and(ViandaSpecifications.tieneCategoria(categoriaEnum));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (filtroViandaDTO.getPrecioMin() != null)
            spec = spec.and(ViandaSpecifications.precioMayorA(filtroViandaDTO.getPrecioMin()));

        if (filtroViandaDTO.getPrecioMax() != null)
            spec = spec.and(ViandaSpecifications.precioMenorA(filtroViandaDTO.getPrecioMax()));

        if (filtroViandaDTO.getNombreVianda() != null)
            spec = spec.and(ViandaSpecifications.nombreContieneIgnoreCase(filtroViandaDTO.getNombreVianda()));

        List<Vianda> viandas = repository.findAll(spec);
        return viandas.stream().map(ViandaDTO::new).toList();
    }

    // --------- mapeo

    private Vianda DTOtoEntity(ViandaCreateDTO viandaDTO) {
        Long id = viandaDTO.getEmprendimientoId();
        Emprendimiento emprendimiento = emprendimientoService.findEntityById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado para el Id: " + id));

        return new Vianda(
                viandaDTO.getNombreVianda(),
                viandaDTO.getCategoria(),
                viandaDTO.getDescripcion(),
                viandaDTO.getPrecio(),
                viandaDTO.getEsVegano(),
                viandaDTO.getEsVegetariano(),
                viandaDTO.getEsSinTacc(),
                emprendimiento
        );
    }
}