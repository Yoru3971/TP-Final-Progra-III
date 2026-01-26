package com.viandasApp.api.Emprendimiento.service;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoAdminDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.repository.EmprendimientoRepository;
import com.viandasApp.api.Emprendimiento.specification.EmprendimientoSpecifications;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmprendimientoServiceImpl implements EmprendimientoService {

    private final EmprendimientoRepository emprendimientoRepository;
    private final UsuarioService usuarioService;
    private final PedidoRepository pedidoRepository; //Uso el repository y no el service para evitar dependencias circulares
    private final CloudinaryService cloudinaryService;
    private final ImageValidationService imageValidationService;
    private final ViandaService viandaService;

    public EmprendimientoServiceImpl(EmprendimientoRepository emprendimientoRepository,
                                     @Lazy UsuarioService usuarioService, // <--- Interfaz aquí
                                     PedidoRepository pedidoRepository,
                                     CloudinaryService cloudinaryService,
                                     ImageValidationService imageValidationService,
                                     @Lazy ViandaService viandaService) {
        this.emprendimientoRepository = emprendimientoRepository;
        this.usuarioService = usuarioService;
        this.pedidoRepository = pedidoRepository;
        this.cloudinaryService = cloudinaryService;
        this.imageValidationService = imageValidationService;
        this.viandaService = viandaService;
    }

    //--------------------------Create--------------------------//
    @Transactional
    @Override
    public EmprendimientoDTO createEmprendimiento(CreateEmprendimientoDTO createEmprendimientoDTO, Usuario usuario) {
        Usuario duenioEmprendimiento = usuarioService.findEntityById(createEmprendimientoDTO.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + createEmprendimientoDTO.getIdUsuario()));

        Long duenioEmprendimientoId = duenioEmprendimiento.getId();

        if (createEmprendimientoDTO.getTelefono().replaceFirst("^0+", "").length() < 7) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El telefono debe tener al menos 7 digitos.");
        }

        boolean esAdmin = usuario.getRolUsuario() == RolUsuario.ADMIN;
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuario.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo podés crear emprendimientos a tu nombre.");
        }

        imageValidationService.validarImagen(createEmprendimientoDTO.getImage(), TipoValidacion.EMPRENDIMIENTO);

        String fotoUrl = cloudinaryService.subirImagen(createEmprendimientoDTO.getImage(), "emprendimientos");

        Emprendimiento emprendimiento = DTOToEntity(createEmprendimientoDTO, fotoUrl);
        Emprendimiento emprendimientoGuardado = emprendimientoRepository.save(emprendimiento);

        return new EmprendimientoDTO(emprendimientoGuardado);
    }

    //--------------------------Read (Paginación)--------------------------//
    @Override
    public Page<Emprendimiento> buscarEmprendimientos(Usuario usuario, String ciudad, String nombre, String nombreDueno, Pageable pageable) {

        Specification<Emprendimiento> spec = Specification.where(null);
        boolean isAdmin = usuario != null && usuario.getRolUsuario() == RolUsuario.ADMIN;
        boolean isDueno = usuario != null && usuario.getRolUsuario() == RolUsuario.DUENO;

        if (isAdmin) {
            if (nombre != null) spec = spec.and(EmprendimientoSpecifications.porNombre(nombre));
            if (ciudad != null) spec = spec.and(EmprendimientoSpecifications.porCiudad(ciudad));
            if (nombreDueno != null) spec = spec.and(EmprendimientoSpecifications.duenoNombreOEmailContiene(nombreDueno));

        } else if (isDueno) {
            spec = spec.and(EmprendimientoSpecifications.perteneceADueno(usuario.getId()));
            spec = spec.and(EmprendimientoSpecifications.noEstaEliminado());

            if (ciudad != null) spec = spec.and(EmprendimientoSpecifications.porCiudad(ciudad));

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay emprendimientos disponibles registrados.");
        }

        return emprendimientos;
    }

    @Override
    public Page<EmprendimientoDTO> getEmprendimientosDisponiblesByCiudad(String ciudad, Pageable pageable) {
        Page<EmprendimientoDTO> page = emprendimientoRepository.findByCiudadAndEstaDisponibleTrue(ciudad, pageable)
                .map(EmprendimientoDTO::new);

        if (page.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron emprendimientos disponibles en: " + ciudad);
        }
        return page;
    }

    @Override
    public Page<EmprendimientoDTO> getEmprendimientosByUsuario(Long idUsuario, Usuario usuario, String ciudad, Pageable pageable) {
        Usuario usuarioEncontrado = usuarioService.findEntityById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + idUsuario));

        boolean esDuenio = usuario.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = idUsuario.equals(usuario.getId());

        if (esDuenio && !esDuenioDelEmprendimiento) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para ver estos emprendimientos.");
        }

        Page<Emprendimiento> resultados;

        if (ciudad != null && !ciudad.isBlank()) {
            resultados = emprendimientoRepository.findByUsuarioIdAndCiudad(idUsuario, ciudad, pageable);
        } else {
            resultados = emprendimientoRepository.findByUsuarioId(idUsuario, pageable);
        }

        if (resultados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron emprendimientos.");
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado con ID: " + id));

        boolean esDuenio = usuario.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = emprendimiento.getUsuario().getId().equals(usuario.getId());

        if (esDuenio && !esDuenioDelEmprendimiento) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para ver este emprendimiento.");
        }
        return Optional.of(new EmprendimientoDTO(emprendimiento));
    }

    @Override
    public Optional<EmprendimientoDTO> getEmprendimientoByIdPublic(Long id) {

        Emprendimiento emprendimiento = emprendimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado con ID: " + id));

        return Optional.of(new EmprendimientoDTO(emprendimiento));
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByNombre(String nombreEmprendimiento) {

        List<EmprendimientoDTO> emprendimientos = emprendimientoRepository.findByNombreEmprendimientoContaining(nombreEmprendimiento)
                .stream()
                .map(EmprendimientoDTO::new)
                .toList();
        if (emprendimientos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron emprendimientos con el nombre: " + nombreEmprendimiento);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron emprendimientos disponibles con el nombre: " + nombreEmprendimiento);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron emprendimientos en la ciudad: " + ciudad);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron emprendimientos disponibles en la ciudad: " + ciudad);
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
                        "Emprendimiento no encontrado con ID: " + id
                ));

        if (updateEmprendimientoDTO.getTelefono().replaceFirst("^0+", "").length() < 7) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El telefono debe tener al menos 7 digitos.");
        }

        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();

        boolean esAdmin = usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para editar este emprendimiento.");
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
                                        "Usuario no encontrado con ID: " + id
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado con ID: " + id));

        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();

        boolean esAdmin = usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para editar este emprendimiento.");
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
    public boolean deleteEmprendimiento(Long id, Usuario usuario) {
        Emprendimiento emprendimiento = emprendimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado con ID: " + id));

        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();
        boolean esAdmin = usuario.getRolUsuario().equals(RolUsuario.ADMIN);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuario.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para eliminar este emprendimiento.");
        }

        procesarEliminacionEmprendimiento(emprendimiento, usuario);

        return true;
    }

    private void procesarEliminacionEmprendimiento(Emprendimiento emprendimiento, Usuario usuario) {
        List<Pedido> pedidosConIdEmprendimiento = pedidoRepository.findByEmprendimientoId(emprendimiento.getId());

        verificarSiTienePedidosActivos(pedidosConIdEmprendimiento);

        if (pedidosConIdEmprendimiento.isEmpty()) {
            emprendimientoRepository.deleteById(emprendimiento.getId());
        } else {
            realizarBajaLogica(emprendimiento, usuario);
            emprendimientoRepository.save(emprendimiento);
        }
    }

    private void verificarSiTienePedidosActivos(List<Pedido> pedidos) {
        boolean tienePedidosEnCurso = pedidos.stream()
                .anyMatch(pedido ->
                        pedido.getEstado() == EstadoPedido.PENDIENTE ||
                                pedido.getEstado() == EstadoPedido.ACEPTADO);

        if (tienePedidosEnCurso) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede eliminar el emprendimiento porque tiene pedidos en curso. Finalizalos o cancelalos antes.");
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

                boolean seraBorradoFisico = vianda.getDetalles().isEmpty();

                viandaService.deleteVianda(vianda.getId(), usuario);

                if (seraBorradoFisico) {
                    emprendimiento.getViandas().remove(vianda);
                }
            }
        }
    }

    private Emprendimiento DTOToEntity(CreateEmprendimientoDTO createEmprendimientoDTO, String imageUrl) {

        Long id = createEmprendimientoDTO.getIdUsuario();
        Usuario usuario = usuarioService.findEntityById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));

        if (!usuario.getRolUsuario().equals(RolUsuario.DUENO) && usuario.getRolUsuario().equals(RolUsuario.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los usuarios con rol DUEÑO/ADMIN pueden crear emprendimientos.");
        }

        return new Emprendimiento(
                createEmprendimientoDTO.getNombreEmprendimiento(),
                createEmprendimientoDTO.getCiudad(),
                createEmprendimientoDTO.getDireccion(),
                createEmprendimientoDTO.getTelefono(),
                usuario,
                imageUrl
        );
    }
}
