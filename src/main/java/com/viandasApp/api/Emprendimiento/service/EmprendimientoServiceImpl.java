package com.viandasApp.api.Emprendimiento.service;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoAdminDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.mappers.EmprendimientoMapper;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.repository.EmprendimientoRepository;
import com.viandasApp.api.Emprendimiento.specification.EmprendimientoSpecifications;
import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.service.NotificacionService;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Pedido.repository.PedidoRepository;
import com.viandasApp.api.ServiceGenerales.cloudinary.CloudinaryService;
import com.viandasApp.api.ServiceGenerales.imageValidation.ImageValidationService;
import com.viandasApp.api.ServiceGenerales.imageValidation.TipoValidacion;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioService;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.service.ViandaService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmprendimientoServiceImpl implements EmprendimientoService {

    private final EmprendimientoRepository emprendimientoRepository;
    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;
    private final PedidoRepository pedidoRepository; //Uso el repository y no el service para evitar dependencias circulares
    private final CloudinaryService cloudinaryService;
    private final ImageValidationService imageValidationService;
    private final ViandaService viandaService;
    private final EmprendimientoMapper emprendimientoMapper;

    public EmprendimientoServiceImpl(EmprendimientoRepository emprendimientoRepository,
                                     @Lazy UsuarioService usuarioService,
                                     @Lazy NotificacionService notificacionService,
                                     PedidoRepository pedidoRepository,
                                     CloudinaryService cloudinaryService,
                                     ImageValidationService imageValidationService,
                                     @Lazy ViandaService viandaService,
                                     EmprendimientoMapper emprendimientoMapper) {
        this.emprendimientoRepository = emprendimientoRepository;
        this.usuarioService = usuarioService;
        this.notificacionService = notificacionService;
        this.pedidoRepository = pedidoRepository;
        this.cloudinaryService = cloudinaryService;
        this.imageValidationService = imageValidationService;
        this.viandaService = viandaService;
        this.emprendimientoMapper = emprendimientoMapper;
    }

    //--------------------------Create--------------------------//
    @Transactional
    @Override
    public EmprendimientoDTO createEmprendimiento(CreateEmprendimientoDTO createEmprendimientoDTO, Usuario usuario) {
        Usuario duenioEmprendimiento = usuarioService.findEntityById(createEmprendimientoDTO.getIdUsuario())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No se encontró un usuario con ID #" + createEmprendimientoDTO.getIdUsuario() + "."
                        )
                );

        Long duenioEmprendimientoId = duenioEmprendimiento.getId();

        if (createEmprendimientoDTO.getTelefono().replaceFirst("^0+", "").length() < 6) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El teléfono debe tener al menos 6 dígitos."
            );
        }

        boolean esAdmin = usuario.getRolUsuario() == RolUsuario.ADMIN;
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuario.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo podés crear emprendimientos a tu nombre."
            );
        }

        if (!duenioEmprendimiento.getRolUsuario().equals(RolUsuario.DUENO)
            && duenioEmprendimiento.getRolUsuario().equals(RolUsuario.ADMIN))
        {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo los usuarios con rol DUEÑO/ADMIN pueden crear emprendimientos."
            );
        }

        imageValidationService.validarImagen(createEmprendimientoDTO.getImage(), TipoValidacion.EMPRENDIMIENTO);

        String fotoUrl = cloudinaryService.subirImagen(createEmprendimientoDTO.getImage(), "emprendimientos");

        Emprendimiento emprendimiento = emprendimientoMapper.DTOToEntity(createEmprendimientoDTO, fotoUrl, duenioEmprendimiento);
        Emprendimiento emprendimientoGuardado = emprendimientoRepository.save(emprendimiento);

        return new EmprendimientoDTO(emprendimientoGuardado);
    }

    //--------------------------Read (Paginación)--------------------------//
    @Override
    public Page<Emprendimiento> buscarEmprendimientos(Usuario usuario, String ciudad, String nombre, String nombreDueno, Boolean soloEliminados, Pageable pageable) {

        Specification<Emprendimiento> spec = Specification.where(null);
        boolean isAdmin = usuario != null && usuario.getRolUsuario() == RolUsuario.ADMIN;
        boolean isDueno = usuario != null && usuario.getRolUsuario() == RolUsuario.DUENO;

        if (isAdmin) {
            if (nombre != null) spec = spec.and(EmprendimientoSpecifications.porNombre(nombre));
            if (ciudad != null) spec = spec.and(EmprendimientoSpecifications.porCiudad(ciudad));
            if (nombreDueno != null) spec = spec.and(EmprendimientoSpecifications.duenoNombreOEmailContiene(nombreDueno));

            if (Boolean.TRUE.equals(soloEliminados)) {
                spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("deletedAt")));
            } else {
                spec = spec.and(EmprendimientoSpecifications.noEstaEliminado());
            }

        } else if (isDueno) {
            spec = spec.and(EmprendimientoSpecifications.perteneceADueno(usuario.getId()));
            spec = spec.and(EmprendimientoSpecifications.noEstaEliminado());

            if (ciudad != null) spec = spec.and(EmprendimientoSpecifications.porCiudad(ciudad));
            if (nombre != null) spec = spec.and(EmprendimientoSpecifications.porNombre(nombre));

        } else {
            spec = spec.and(EmprendimientoSpecifications.estaDisponible(true));
            spec = spec.and(EmprendimientoSpecifications.noEstaEliminado());

            if (ciudad != null) spec = spec.and(EmprendimientoSpecifications.porCiudad(ciudad));
            if (nombre != null) spec = spec.and(EmprendimientoSpecifications.porNombre(nombre));
        }

        if (pageable.getSort().isUnsorted()) {
            if (isAdmin) {
                spec = spec.and(EmprendimientoSpecifications.ordenamientoAdmin());
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            } else {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("nombreEmprendimiento"));
            }
        }

        return emprendimientoRepository.findAll(spec, pageable);
    }

    @Override
    public Page<EmprendimientoDTO> getAllEmprendimientosDisponibles(Pageable pageable) {
        Page<EmprendimientoDTO> emprendimientos = emprendimientoRepository.findByEstaDisponibleTrue(pageable)
                .map(EmprendimientoDTO::new);

        if (emprendimientos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontraron emprendimientos disponibles."
            );
        }

        return emprendimientos;
    }

    @Override
    public Page<EmprendimientoDTO> getEmprendimientosDisponiblesByCiudad(String ciudad, Pageable pageable) {
        Page<EmprendimientoDTO> page = emprendimientoRepository.findByCiudadAndEstaDisponibleTrue(ciudad, pageable)
                .map(EmprendimientoDTO::new);

        if (page.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontraron emprendimientos disponibles en " + ciudad + "."
            );
        }
        return page;
    }

    @Override
    public Page<EmprendimientoDTO> getEmprendimientosByUsuario(Long idUsuario, Usuario usuario, String ciudad, Pageable pageable) {
        Usuario usuarioEncontrado = usuarioService.findEntityById(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + idUsuario + "."
                        )
                );

        boolean esDuenio = usuario.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = idUsuario.equals(usuario.getId());

        if (esDuenio && !esDuenioDelEmprendimiento) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permiso para ver estos emprendimientos."
            );
        }

        Page<Emprendimiento> resultados;

        if (ciudad != null && !ciudad.isBlank()) {
            resultados = emprendimientoRepository.findByUsuarioIdAndCiudad(idUsuario, ciudad, pageable);
        } else {
            resultados = emprendimientoRepository.findByUsuarioId(idUsuario, pageable);
        }

        if (resultados.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontraron emprendimientos para este usuario."
            );
        }

        return resultados.map(EmprendimientoDTO::new);
    }

    //--------------------------Read--------------------------//
    @Override
    public List<EmprendimientoDTO> getAllEmprendimientos() {

        List<EmprendimientoDTO> emprendimientos = emprendimientoRepository.findAll().stream()
                .map(EmprendimientoDTO::new)
                .toList();

        if (emprendimientos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay emprendimientos registrados.");
        }

        return emprendimientos;
    }

    @Override
    public Optional<EmprendimientoDTO> getEmprendimientoById(Long id, Usuario usuario) {

        Emprendimiento emprendimiento = emprendimientoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un emprendimiento con ID #" + id + "."
                        )
                );

        boolean esDuenio = usuario.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = emprendimiento.getUsuario().getId().equals(usuario.getId());

        if (esDuenio && !esDuenioDelEmprendimiento) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permiso para ver este emprendimiento."
            );
        }
        return Optional.of(new EmprendimientoDTO(emprendimiento));
    }

    @Override
    public Optional<EmprendimientoDTO> getEmprendimientoByIdPublic(Long id) {

        Emprendimiento emprendimiento = emprendimientoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un emprendimiento con ID #" + id + "."
                        )
                );

        return Optional.of(new EmprendimientoDTO(emprendimiento));
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByNombre(String nombreEmprendimiento) {

        List<EmprendimientoDTO> emprendimientos = emprendimientoRepository.findByNombreEmprendimientoContaining(nombreEmprendimiento)
                .stream()
                .map(EmprendimientoDTO::new)
                .toList();
        if (emprendimientos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontraron emprendimientos con el nombre \"" + nombreEmprendimiento + "\"."
            );
        }

        return emprendimientos;
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosDisponiblesByNombre(String nombreEmprendimiento) {

        List<EmprendimientoDTO> emprendimientos = emprendimientoRepository.findByNombreEmprendimientoContainingAndEstaDisponibleTrue(nombreEmprendimiento)
                .stream()
                .map(EmprendimientoDTO::new)
                .toList();
        if (emprendimientos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontraron emprendimientos disponibles con el nombre \"" + nombreEmprendimiento + "\"."
            );
        }

        return emprendimientos;
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByCiudad(String ciudad) {
        List<EmprendimientoDTO> emprendimientos = emprendimientoRepository.findByCiudad(ciudad)
                .stream()
                .map(EmprendimientoDTO::new)
                .toList();
        if (emprendimientos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontraron emprendimientos en " + ciudad + "."
            );
        }

        return emprendimientos;
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosDisponiblesByCiudad(String ciudad) {
        List<EmprendimientoDTO> emprendimientos = emprendimientoRepository.findByCiudadAndEstaDisponibleTrue(ciudad)
                .stream()
                .map(EmprendimientoDTO::new)
                .toList();
        if (emprendimientos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontraron emprendimientos disponibles en " + ciudad + "."
            );
        }

        return emprendimientos;
    }

    @Override
    public Page<EmprendimientoAdminDTO> getAllEmprendimientosForAdmin(Pageable pageable) {
        return emprendimientoRepository.findAll(pageable)
                .map(EmprendimientoAdminDTO::new);
    }

    //--------------------------Update--------------------------//
    @Transactional
    @Override
    public Optional<EmprendimientoDTO> updateEmprendimiento(
            Long id,
            UpdateEmprendimientoDTO updateEmprendimientoDTO,
            Usuario usuarioLogueado
    ) {

        Emprendimiento emprendimiento = emprendimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró un emprendimiento con ID #" + id + "."
                ));

        if (updateEmprendimientoDTO.getTelefono().replaceFirst("^0+", "").length() < 6) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El teléfono debe tener al menos 6 dígitos."
            );
        }

        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();

        boolean esAdmin = usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permiso para modificar este emprendimiento."
            );
        }

        return emprendimientoRepository.findById(id)
                .map(emprendimientoExistente -> {

                    if (updateEmprendimientoDTO.getNombreEmprendimiento() != null) {
                        emprendimientoExistente.setNombreEmprendimiento(updateEmprendimientoDTO.getNombreEmprendimiento());
                    }

                    if (updateEmprendimientoDTO.getCiudad() != null) {
                        emprendimientoExistente.setCiudad(updateEmprendimientoDTO.getCiudad());
                    }

                    if (updateEmprendimientoDTO.getDireccion() != null) {
                        emprendimientoExistente.setDireccion(updateEmprendimientoDTO.getDireccion());
                    }

                    if (updateEmprendimientoDTO.getTelefono() != null) {
                        emprendimientoExistente.setTelefono(updateEmprendimientoDTO.getTelefono());
                    }

                    if (updateEmprendimientoDTO.getEstaDisponible() != null) {
                        emprendimientoExistente.setEstaDisponible(updateEmprendimientoDTO.getEstaDisponible());
                    }

                    if (updateEmprendimientoDTO.getIdUsuario() != null) {

                        Usuario usuario = usuarioService.findEntityById(updateEmprendimientoDTO.getIdUsuario())
                                .orElseThrow(() -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No se encontró un usuario con ID #" + id + "."
                                ));

                        if (!usuario.getRolUsuario().equals(RolUsuario.DUENO)) {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                    "Solo los usuarios con rol DUEÑO pueden tener emprendimientos.");
                        }

                        emprendimientoExistente.setUsuario(usuario);
                    }

                    Emprendimiento actualizado = emprendimientoRepository.save(emprendimientoExistente);
                    return new EmprendimientoDTO(actualizado);
                });
    }

    /// La actualizacion de la imagen debemos hacer desde otro end-point pq se comunica directamente con cloudinary
    @Transactional
    @Override
    public EmprendimientoDTO updateImagenEmprendimiento(Long id, MultipartFile image, Usuario usuarioLogueado) {
        Emprendimiento emprendimiento = emprendimientoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un emprendimiento con ID #" + id + "."
                        )
                );

        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();

        boolean esAdmin = usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permiso para modificar la imagen de este emprendimiento."
            );
        }

        imageValidationService.validarImagen(image, TipoValidacion.EMPRENDIMIENTO);

        String fotoUrl = cloudinaryService.subirImagen(image, "emprendimientos");
        emprendimiento.setImagenUrl(fotoUrl);
        emprendimientoRepository.save(emprendimiento);
        return new EmprendimientoDTO(emprendimiento);
    }

    //--------------------------Delete--------------------------//
    @Transactional
    @Override
    public boolean deleteEmprendimiento(Long id, Usuario usuario, boolean forzar) {
        Emprendimiento emprendimiento = emprendimientoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un emprendimiento con ID #" + id + "."
                        )
                );

        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();
        boolean esAdmin = usuario.getRolUsuario().equals(RolUsuario.ADMIN);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuario.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permiso para eliminar este emprendimiento."
            );
        }

        procesarEliminacionEmprendimiento(emprendimiento, usuario, forzar);

        return true;
    }

    private void procesarEliminacionEmprendimiento(Emprendimiento emprendimiento, Usuario usuario, boolean forzar) {
        List<Pedido> pedidosConIdEmprendimiento = pedidoRepository.findByEmprendimientoId(emprendimiento.getId());

        if (forzar) {
            cancelarPedidosActivos(pedidosConIdEmprendimiento);
        }
        else {
            verificarSiTienePedidosActivos(pedidosConIdEmprendimiento, usuario);
        }

        if (pedidosConIdEmprendimiento.isEmpty()) {
            emprendimientoRepository.deleteById(emprendimiento.getId());
        } else {
            realizarBajaLogica(emprendimiento, usuario);
            emprendimientoRepository.save(emprendimiento);
        }
    }

    private void verificarSiTienePedidosActivos(List<Pedido> pedidos, Usuario usuarioLogueado) {
        boolean tienePedidosEnCurso = pedidos.stream()
                .anyMatch(pedido ->
                        pedido.getEstado() == EstadoPedido.PENDIENTE ||
                                pedido.getEstado() == EstadoPedido.ACEPTADO);

        if (tienePedidosEnCurso) {
            boolean esAdmin = usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN);

            String sujeto = esAdmin ? "el emprendimiento" : "este emprendimiento";
            String accion = esAdmin ? "Deben finalizarse o cancelarse" : "Finalizalos o cancelalos";

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar " + sujeto + " porque tiene pedidos en proceso. " + accion + " antes."
            );
        }
    }

    private void cancelarPedidosActivos(List<Pedido> pedidos) {
        for (var pedido : pedidos) {
            var estado = pedido.getEstado();

            if (estado == EstadoPedido.PENDIENTE || estado == EstadoPedido.ACEPTADO) {
                pedido.setEstado(EstadoPedido.CANCELADO);
                pedidoRepository.save(pedido);

                String mensaje =
                        "El pedido #" + pedido.getId() + " fue cancelado porque el emprendimiento fue eliminado.";

                // Para dueño
                notificacionService.createNotificacion(
                        new NotificacionCreateDTO(
                                pedido.getEmprendimiento().getUsuario().getId(),
                                pedido.getEmprendimiento().getId(),
                                mensaje,
                                LocalDate.now()
                        )
                );

                // Para cliente
                notificacionService.createNotificacion(
                        new NotificacionCreateDTO(
                                pedido.getUsuario().getId(),
                                pedido.getEmprendimiento().getId(),
                                mensaje,
                                LocalDate.now()
                        )
                );
            }
        }
    }

    //--------------------------Otros--------------------------//
    @Override
    public Optional<Emprendimiento> findEntityById(Long id) {

        return emprendimientoRepository.findById(id);
    }

    private void realizarBajaLogica(Emprendimiento emprendimiento, Usuario usuario) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        emprendimiento.setNombreEmprendimiento("Emprendimiento Eliminado_" + timestamp + "_" + emprendimiento.getNombreEmprendimiento());
        emprendimiento.setDireccion("Emprendimiento Eliminado_" + timestamp + "_" + emprendimiento.getDireccion());
        emprendimiento.setCiudad("Emprendimiento Eliminado_" + timestamp + "_" + emprendimiento.getCiudad());
        emprendimiento.setTelefono("Emprendimiento Eliminado_" + timestamp + "_" + emprendimiento.getTelefono());
        emprendimiento.setImagenUrl("https://res.cloudinary.com/dsgqbotzi/image/upload/v1767913818/Gemini_Generated_Image_8mvsmh8mvsmh8mvs_wrkvgg.png");

        emprendimiento.setDeletedAt(LocalDateTime.now());
        emprendimiento.setEstaDisponible(false);

        if (emprendimiento.getViandas() != null) {
            List<Vianda> viandasCopia = new ArrayList<>(emprendimiento.getViandas());

            for (Vianda vianda : viandasCopia) {

                if (vianda.getDeletedAt() != null) {
                    continue;
                }
                
                boolean seraBorradoFisico = vianda.getDetalles().isEmpty();

                viandaService.deleteVianda(vianda.getId(), usuario);

                if (seraBorradoFisico) {
                    emprendimiento.getViandas().remove(vianda);
                }
            }
        }
    }

}
