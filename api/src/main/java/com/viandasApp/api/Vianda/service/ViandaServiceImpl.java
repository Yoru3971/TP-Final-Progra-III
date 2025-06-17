package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
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

    @Override
    public ViandaDTO createVianda(ViandaCreateDTO dto, Usuario usuarioLogueado) {
        Emprendimiento emprendimiento = emprendimientoService.findEntityById(dto.getEmprendimientoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado"));

        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();

        boolean esAdmin = usuarioLogueado.getRolUsuario() == RolUsuario.ADMIN;
        boolean esDuenio = usuarioLogueado.getRolUsuario() == RolUsuario.DUENO;
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if (esDuenio && !esDuenioDelEmprendimiento || !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para editar esta vianda.");
        }

        Vianda vianda = DTOtoEntity(dto);
        vianda.setEmprendimiento(emprendimiento);
        Vianda nuevaVianda = repository.save(vianda);
        return new ViandaDTO(nuevaVianda);
    }

    @Override
    public Optional<ViandaDTO> findViandaById(Long id, Usuario usuarioLogueado) {
        Optional<Vianda> viandaOptional = repository.findById(id);

        if (viandaOptional.isEmpty()) {
            return Optional.empty();
        }

        Vianda vianda = viandaOptional.get();
        Emprendimiento emprendimiento = emprendimientoService
                .findEntityById(vianda.getEmprendimiento().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Emprendimiento no encontrado para el Id: " + vianda.getEmprendimiento().getId()));

        vianda.setEmprendimiento(emprendimiento);
        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();

        boolean esDuenio = usuarioLogueado.getRolUsuario() == RolUsuario.DUENO;
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if (esDuenio && !esDuenioDelEmprendimiento) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para editar esta vianda.");
        }

        return Optional.of(new ViandaDTO(vianda));
    }

    @Override
    public Optional<ViandaDTO> updateVianda(Long id, ViandaUpdateDTO dto, Usuario usuarioLogueado) {
        Optional<Vianda> optionalVianda = repository.findById(id);

        if (optionalVianda.isEmpty()) return Optional.empty();

        Vianda viandaActual = optionalVianda.get();

        Long duenioEmprendimientoId = viandaActual.getEmprendimiento().getUsuario().getId();

        boolean esAdmin = usuarioLogueado.getRolUsuario() == RolUsuario.ADMIN;
        boolean esDuenio = usuarioLogueado.getRolUsuario() == RolUsuario.DUENO;
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if (esDuenio && !esDuenioDelEmprendimiento || !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para editar esta vianda.");
        }

        if (dto.getNombreVianda() != null) viandaActual.setNombreVianda(dto.getNombreVianda());
        if (dto.getCategoria() != null) viandaActual.setCategoria(dto.getCategoria());
        if (dto.getDescripcion() != null) viandaActual.setDescripcion(dto.getDescripcion());
        if (dto.getPrecio() != null) viandaActual.setPrecio(dto.getPrecio());
        if (dto.getEsVegano() != null) viandaActual.setEsVegano(dto.getEsVegano());
        if (dto.getEsVegetariano() != null) viandaActual.setEsVegetariano(dto.getEsVegetariano());
        if (dto.getEsSinTacc() != null) viandaActual.setEsSinTacc(dto.getEsSinTacc());
        if (dto.getEstaDisponible() != null) viandaActual.setEstaDisponible(dto.getEstaDisponible());

        Vianda nuevaVianda = repository.save(viandaActual);
        return Optional.of(new ViandaDTO(nuevaVianda));
    }


    @Override
    public boolean deleteVianda(Long id, Usuario usuarioLogueado) {
        Optional<Vianda> optionalVianda = repository.findById(id);

        if (optionalVianda.isEmpty()) return false;

        Vianda vianda = optionalVianda.get();

        Long duenioEmprendimientoId = vianda.getEmprendimiento().getUsuario().getId();

        boolean esAdmin = usuarioLogueado.getRolUsuario() == RolUsuario.ADMIN;
        boolean esDuenio = usuarioLogueado.getRolUsuario() == RolUsuario.DUENO;
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if (esDuenio && !esDuenioDelEmprendimiento || !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para eliminar esta vianda.");
        }

        repository.delete(vianda);
        return true;
    }

    //  ---------   listado

    @Override
    public List<ViandaDTO> getViandasByEmprendimiento(
            FiltroViandaDTO filtroViandaDTO,
            Long idEmprendimiento,
            Usuario usuario) {

        Optional<Emprendimiento> emprendimientoOptional = emprendimientoService.findEntityById(idEmprendimiento);

        if (emprendimientoOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado para el Id: " + idEmprendimiento);
        }

        Emprendimiento emprendimiento = emprendimientoOptional.get();

        boolean esAdmin = usuario.getRolUsuario() == RolUsuario.ADMIN;
        boolean esDuenio = usuario.getRolUsuario() == RolUsuario.DUENO;
        boolean esDuenioDelEmprendimiento = emprendimiento.getUsuario().getId().equals(usuario.getId());

        if (esDuenio && !esDuenioDelEmprendimiento || !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para ver las viandas de este emprendimiento.");
        }

        Specification<Vianda> spec = ViandaSpecifications
                .perteneceAEmprendimiento(idEmprendimiento);

        if (filtroViandaDTO.getEstaDisponible() != null)
            spec = spec.and(ViandaSpecifications.estaDisponible(filtroViandaDTO.getEstaDisponible()));

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

    @Override
    public List<ViandaDTO> getViandasDisponiblesByEmprendimiento(
            FiltroViandaDTO filtroViandaDTO,
            Long idEmprendimiento) {

        Specification<Vianda> spec = ViandaSpecifications
                .estaDisponible()
                .and(ViandaSpecifications.perteneceAEmprendimiento(idEmprendimiento));

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

    @Override
    public Optional<Vianda> findEntityViandaById(Long id) {
        return repository.findById(id);
    }
}