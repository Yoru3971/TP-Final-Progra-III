package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.ServiceGenerales.CloudinaryService;
import com.viandasApp.api.ServiceGenerales.ImageValidationService;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.*;
import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.repository.ViandaRepository;
import com.viandasApp.api.Vianda.specification.ViandaSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ViandaServiceImpl implements ViandaService {

    private final ViandaRepository viandaRepository;
    private final EmprendimientoServiceImpl emprendimientoService;
    private final CloudinaryService cloudinaryService;
    private final ImageValidationService imageValidationService;

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

        imageValidationService.validarImagen(dto.getImage(), ImageValidationService.TipoValidacion.VIANDA);

        String fotoUrl = cloudinaryService.subirImagen(dto.getImage(), "viandas");

        Vianda vianda = DTOtoEntity(dto, fotoUrl);
        vianda.setEmprendimiento(emprendimiento);
        Vianda nuevaVianda = viandaRepository.save(vianda);
        return new ViandaDTO(nuevaVianda);
    }

    //--------------------------Read--------------------------//
    @Override
    public Page<Vianda> getViandasByEmprendimiento(FiltroViandaDTO filtroViandaDTO, Long idEmprendimiento, Usuario usuario, boolean incluirEliminadas, Pageable pageable) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado para el Id: " + idEmprendimiento));

        boolean esAdmin = usuario.getRolUsuario().equals(RolUsuario.ADMIN);
        boolean esDuenio = usuario.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = emprendimiento.getUsuario().getId().equals(usuario.getId());

        if ( esDuenio && !esDuenioDelEmprendimiento ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para ver las viandas de este emprendimiento.");
        }

        //  Filtro con Specifications
        Specification<Vianda> spec = ViandaSpecifications.perteneceAEmprendimiento(idEmprendimiento);
        if (!incluirEliminadas) {
            spec = spec.and(ViandaSpecifications.noEstaEliminada());
        }
        spec = aplicarFiltrosComunes(spec, filtroViandaDTO);

        //  Ordeno (por nombre, disponibilidad y eliminación)
        if (pageable.getSort().isUnsorted()) {
            Sort defaultSort;
            if (incluirEliminadas) {
                // ADMIN: Disponibles -> No disponibles -> Eliminadas
                defaultSort = Sort.by(
                        Sort.Order.asc("deletedAt"),
                        Sort.Order.desc("estaDisponible"),
                        Sort.Order.asc("nombreVianda")
                );
            } else {
                // DUEÑO: Disponibles -> No disponibles
                defaultSort = Sort.by(Sort.Direction.DESC, "estaDisponible")
                        .and(Sort.by(Sort.Direction.ASC, "nombreVianda"));
            }
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }


        Page<Vianda> viandas = viandaRepository.findAll(spec, pageable);
        if (viandas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron viandas para el emprendimiento con ID: " + idEmprendimiento);
        }
        return viandas;
    }

    @Override
    public Page<ViandaDTO> getViandasDisponiblesByEmprendimiento(FiltroViandaDTO filtroViandaDTO, Long idEmprendimiento, Pageable pageable) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado para el Id: " + idEmprendimiento));

        //  Filtro con Specifications
        Specification<Vianda> spec = ViandaSpecifications
                .estaDisponible()
                .and(ViandaSpecifications.perteneceAEmprendimiento(idEmprendimiento));
        spec = spec.and(ViandaSpecifications.noEstaEliminada());
        spec = aplicarFiltrosComunes(spec, filtroViandaDTO);

        //  Ordeno (por nombre)
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "nombreVianda"));
        }


        Page<Vianda> viandas = viandaRepository.findAll(spec, pageable);
        if (viandas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron viandas disponibles para el emprendimiento con ID: " + idEmprendimiento);
        }
        return viandas.map(ViandaDTO::new);
    }

    @Override
    public Optional<ViandaDTO> findViandaById(Long id, Usuario usuarioLogueado) {

        Vianda vianda = viandaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada para el Id: " + id));

        if (vianda.getDeletedAt() != null && !usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada para el Id: " + id);
        }

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

        if (vianda.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada para el Id: " + id);
        }

        return Optional.of(new ViandaDTO(vianda));
    }

    @Override
    public Page<ViandaAdminDTO> getAllViandasForAdmin(Pageable pageable) {
        return viandaRepository.findAll(pageable)
                .map(ViandaAdminDTO::new);
    }

    @Override
    public List<CategoriaVianda> getCategoriasByEmprendimiento(Long idEmprendimiento, Usuario usuario) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(idEmprendimiento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado para el Id: " + idEmprendimiento));

        if (usuario == null) {
            return viandaRepository.findCategoriasByEmprendimientoIdPublic(idEmprendimiento);
        }

        if (usuario.getRolUsuario().equals(RolUsuario.ADMIN)) {
            return viandaRepository.findCategoriasByEmprendimientoIdAdmin(idEmprendimiento);
        }

        if (usuario.getRolUsuario().equals(RolUsuario.DUENO)) {
            boolean esSuEmprendimiento = emprendimiento.getUsuario().getId().equals(usuario.getId());
            if (esSuEmprendimiento) {
                return viandaRepository.findCategoriasByEmprendimientoIdOwner(idEmprendimiento);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para ver información de este emprendimiento.");
            }
        }
        return viandaRepository.findCategoriasByEmprendimientoIdPublic(idEmprendimiento);
    }

    //--------------------------Update--------------------------//
    @Transactional
    @Override
    public Optional<ViandaDTO> updateVianda(Long id, ViandaUpdateDTO dto, Usuario usuarioLogueado) {

        Vianda vianda = viandaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada para el Id: " + id));

        if (vianda.getDeletedAt() != null && !usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada.");
        }

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

        imageValidationService.validarImagen(image, ImageValidationService.TipoValidacion.VIANDA);

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

        if (vianda.getDeletedAt() != null && !usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vianda no encontrada.");
        }

        Long duenioEmprendimientoId = vianda.getEmprendimiento().getUsuario().getId();
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());
        boolean esAdmin = usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN);

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para eliminar esta vianda.");
        }

        procesarEliminacionVianda(vianda);

        return true;
    }

    private void procesarEliminacionVianda(Vianda vianda) {
        verificarSiTienePedidosActivos(vianda);

        if (vianda.getDetalles().isEmpty()) {
            viandaRepository.delete(vianda);
        } else {
            realizarBajaLogica(vianda);
            viandaRepository.save(vianda);
        }
    }

    private void verificarSiTienePedidosActivos(Vianda vianda) {
        boolean tienePedidosActivos = vianda.getDetalles().stream()
                .anyMatch(detalle -> {
                    EstadoPedido estado = detalle.getPedido().getEstado();
                    return estado == EstadoPedido.PENDIENTE || estado == EstadoPedido.ACEPTADO;
                });

        if (tienePedidosActivos) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede eliminar la vianda porque está en un pedido en curso (Pendiente o Aceptado).");
        }
    }

    //--------------------------Otros--------------------------//
    @Override
    public Optional<Vianda> findEntityViandaById(Long id) {

        return viandaRepository.findById(id);
    }

    private void realizarBajaLogica(Vianda vianda) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        vianda.setNombreVianda("Vianda Eliminada_" + timestamp + "_" + vianda.getNombreVianda());
        vianda.setDescripcion("Vianda Eliminada_" + timestamp + "_" + vianda.getDescripcion());
        vianda.setPrecio(0.0);

        vianda.setImagenUrl("https://res.cloudinary.com/dsgqbotzi/image/upload/v1767926581/default_vianda_rb2ila.png");

        vianda.setDeletedAt(LocalDateTime.now());
        vianda.setEstaDisponible(false);
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

    private Specification<Vianda> aplicarFiltrosComunes(Specification<Vianda> spec, FiltroViandaDTO filtro) {
        if (filtro.getEstaDisponible() != null)
            spec = spec.and(ViandaSpecifications.estaDisponible(filtro.getEstaDisponible()));

        if (filtro.getEsVegano() != null)
            spec = spec.and(ViandaSpecifications.esVegana(filtro.getEsVegano()));

        if (filtro.getEsVegetariano() != null)
            spec = spec.and(ViandaSpecifications.esVegetariana(filtro.getEsVegetariano()));

        if (filtro.getEsSinTacc() != null)
            spec = spec.and(ViandaSpecifications.esSinTacc(filtro.getEsSinTacc()));

        if (filtro.getCategoria() != null) {
            try {
                CategoriaVianda categoriaEnum = CategoriaVianda.fromDescripcion(filtro.getCategoria());
                spec = spec.and(ViandaSpecifications.tieneCategoria(categoriaEnum));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (filtro.getPrecioMin() != null)
            spec = spec.and(ViandaSpecifications.precioMayorA(filtro.getPrecioMin()));

        if (filtro.getPrecioMax() != null)
            spec = spec.and(ViandaSpecifications.precioMenorA(filtro.getPrecioMax()));

        if (filtro.getNombreVianda() != null)
            spec = spec.and(ViandaSpecifications.nombreContieneIgnoreCase(filtro.getNombreVianda()));

        return spec;
    }
}