package com.viandasApp.api.Vianda.service;

import com.cloudinary.utils.ObjectUtils;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
import com.viandasApp.api.ServiceGenerales.CloudinaryService;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ViandaServiceImpl implements ViandaService {

    private final ViandaRepository viandaRepository;
    private final EmprendimientoServiceImpl emprendimientoService;
    private final CloudinaryService cloudinaryService;

    //--------------------------Create--------------------------//
    @Transactional
    @Override
    public ViandaDTO createVianda(ViandaCreateDTO dto, Usuario usuarioLogueado) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(dto.getEmprendimientoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado para el Id: " + dto.getEmprendimientoId()));

        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();

        boolean esDuenio = usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if ( esDuenio && !esDuenioDelEmprendimiento ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para crear esta vianda.");
        }

        //Subir imagen a Cloudinary
        String fotoUrl = cloudinaryService.subirImagen(dto.getImage(), "viandas");

        Vianda vianda = DTOtoEntity(dto, fotoUrl);
        vianda.setEmprendimiento(emprendimiento);
        Vianda nuevaVianda = viandaRepository.save(vianda);
        return new ViandaDTO(nuevaVianda);
    }

    //--------------------------Read--------------------------//
    @Override
    public List<ViandaDTO> getViandasByEmprendimiento(FiltroViandaDTO filtroViandaDTO, Long idEmprendimiento, Usuario usuario) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado para el Id: " + idEmprendimiento));

        boolean esAdmin = usuario.getRolUsuario().equals(RolUsuario.ADMIN);
        boolean esDuenio = usuario.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = emprendimiento.getUsuario().getId().equals(usuario.getId());

        if ( esDuenio && !esDuenioDelEmprendimiento ) {
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

        List<Vianda> viandas = viandaRepository.findAll(spec);
        if (viandas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron viandas para el emprendimiento con ID: " + idEmprendimiento);
        }
        return viandas.stream()
                        .map(ViandaDTO::new)
                        .toList();
    }

    @Override
    public List<ViandaDTO> getViandasDisponiblesByEmprendimiento(FiltroViandaDTO filtroViandaDTO, Long idEmprendimiento) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado para el Id: " + idEmprendimiento));

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

        List<Vianda> viandas = viandaRepository.findAll(spec);
        if (viandas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron viandas disponibles para el emprendimiento con ID: " + idEmprendimiento);
        }
        return viandas.stream()
                        .map(ViandaDTO::new)
                        .toList();
    }

    @Override
    public Optional<ViandaDTO> findViandaById(Long id, Usuario usuarioLogueado) {

        Vianda vianda = viandaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada para el Id: " + id));

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(vianda.getEmprendimiento().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado para el Id: " + vianda.getEmprendimiento().getId()));

        vianda.setEmprendimiento(emprendimiento);
        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();

        boolean esDuenio = usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if ( esDuenio && !esDuenioDelEmprendimiento ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para ver esta vianda.");
        }
        return Optional.of(new ViandaDTO(vianda));
    }

    @Override
    public Optional<ViandaDTO> findViandaByIdPublic(Long id) {

        Vianda vianda = viandaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada para el Id: " + id));

        return Optional.of(new ViandaDTO(vianda));
    }

    //--------------------------Update--------------------------//
    @Transactional
    @Override
    public Optional<ViandaDTO> updateVianda(Long id, ViandaUpdateDTO dto, Usuario usuarioLogueado) {

        Vianda vianda = viandaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada para el Id: " + id));

        Long duenioEmprendimientoId = vianda.getEmprendimiento().getUsuario().getId();

        boolean esDuenio = usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if ( esDuenio && !esDuenioDelEmprendimiento ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para editar esta vianda.");
        }

        if (dto.getNombreVianda() != null) vianda.setNombreVianda(dto.getNombreVianda());
        if (dto.getCategoria() != null) vianda.setCategoria(dto.getCategoria());
        if (dto.getDescripcion() != null) vianda.setDescripcion(dto.getDescripcion());
        if (dto.getPrecio() != null) vianda.setPrecio(dto.getPrecio());
        if (dto.getEsVegano() != null) vianda.setEsVegano(dto.getEsVegano());
        if (dto.getEsVegetariano() != null) vianda.setEsVegetariano(dto.getEsVegetariano());
        if (dto.getEsSinTacc() != null) vianda.setEsSinTacc(dto.getEsSinTacc());
        if (dto.getEstaDisponible() != null) vianda.setEstaDisponible(dto.getEstaDisponible());

        Vianda nuevaVianda = viandaRepository.save(vianda);
        return Optional.of(new ViandaDTO(nuevaVianda));
    }
    /// La actualizacion de la imagen debemos hacer desde otro end-point pq se comunica directamente con cloudinary

    @Transactional
    @Override
    public ViandaDTO updateImagenVianda(Long id, MultipartFile image, Usuario usuarioLogueado) {
        Vianda vianda = viandaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada."));

        if (usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO) &&
            !vianda.getEmprendimiento().getUsuario().getId().equals(usuarioLogueado.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenes permiso para editar esta vianda.");
        }

        String fotoUrl = cloudinaryService.subirImagen(image, "viandas");

        vianda.setImagenUrl(fotoUrl);
        viandaRepository.save(vianda);
        return new ViandaDTO(vianda);
    }

    //--------------------------Delete--------------------------//
    @Transactional
    @Override
    public boolean deleteVianda(Long id, Usuario usuarioLogueado) {
        Vianda vianda = viandaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada para el Id: " + id));

        Long duenioEmprendimientoId = vianda.getEmprendimiento().getUsuario().getId();

        boolean esDuenio = usuarioLogueado.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if (esDuenio && !esDuenioDelEmprendimiento) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para eliminar esta vianda.");
        }

        // Validación para evitar borrar viandas en pedidos
        if (vianda.getDetalles() != null && !vianda.getDetalles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede eliminar la vianda porque está asociada a uno o más pedidos.");
        }

        viandaRepository.delete(vianda);
        return true;
    }

    //--------------------------Otros--------------------------//
    @Override
    public Optional<Vianda> findEntityViandaById(Long id) {

        return viandaRepository.findById(id);
    }

    private Vianda DTOtoEntity(ViandaCreateDTO viandaDTO, String fotoUrl) {

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
                emprendimiento,
                fotoUrl
        );
    }
}