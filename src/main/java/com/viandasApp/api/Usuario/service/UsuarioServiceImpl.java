package com.viandasApp.api.Usuario.service;

import com.viandasApp.api.Auth.repository.RefreshTokenRepository;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.repository.EmprendimientoRepository;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoService;
import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.service.NotificacionService;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Pedido.repository.PedidoRepository;
import com.viandasApp.api.ServiceGenerales.cloudinary.CloudinaryService;
import com.viandasApp.api.ServiceGenerales.imageValidation.ImageValidationService;
import com.viandasApp.api.ServiceGenerales.imageValidation.TipoValidacion;
import com.viandasApp.api.Usuario.dto.*;
import com.viandasApp.api.Usuario.mappers.UsuarioMapper;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import com.viandasApp.api.Usuario.specification.UsuarioSpecifications;
import com.viandasApp.api.Vianda.repository.ViandaRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmprendimientoRepository emprendimientoRepository;
    private final PedidoRepository pedidoRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final EmprendimientoService emprendimientoService;
    private final NotificacionService notificacionService;
    private final ImageValidationService imageValidationService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              EmprendimientoRepository emprendimientoRepository,
                              @Lazy EmprendimientoService emprendimientoService,
                              @Lazy NotificacionService notificacionService,
                              PedidoRepository pedidoRepository,
                              PasswordEncoder passwordEncoder,
                              CloudinaryService cloudinaryService,
                              ImageValidationService imageValidationService,
                              UsuarioMapper usuarioMapper,
                              RefreshTokenRepository refreshTokenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.emprendimientoRepository = emprendimientoRepository;
        this.emprendimientoService = emprendimientoService;
        this.notificacionService = notificacionService;
        this.pedidoRepository = pedidoRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
        this.imageValidationService = imageValidationService;
        this.usuarioMapper = usuarioMapper;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    //--------------------------Create--------------------------//
    @Override
    @Transactional
    public UsuarioAdminDTO createUsuario(UsuarioCreateDTO usuarioCreateDTO) {

        Usuario usuario = usuarioMapper.DTOToEntity(usuarioCreateDTO);

        Usuario usuarioLogueado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Verifica si ya existe un usuario con el mismo email
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un usuario con el email " + usuario.getEmail() + "."
            );
        }

        String telefonoSinCeros = usuarioCreateDTO.getTelefono().replaceFirst("^0+", "");
        if (telefonoSinCeros.length() < 6) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El teléfono debe tener al menos 6 dígitos."
            );
        }
        usuario.setTelefono(telefonoSinCeros);

        // Verifica si ya existe un usuario con el mismo telefono
        if (usuarioRepository.findByTelefono(usuario.getTelefono()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un usuario con el teléfono " + usuario.getTelefono() + "."
            );
        }

        // Verifica que el rol del usuario logueado sea ADMIN si el nuevo usuario es ADMIN, de esta manera se evita que un usuario
        // normal pueda registrarse como ADMIN, pero permite que el ADMIN registre nuevos usuarios como ADMIN.
        if (usuarioCreateDTO.getRolUsuario().equals(RolUsuario.ADMIN) && !usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No podes registrarte como ADMIN. Este rol es exclusivo de los administradores del sistema."
            );
        }

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return new UsuarioAdminDTO(savedUsuario);
    }

    //--------------------------Read--------------------------//

    @Override
    public Page<UsuarioAdminDTO> buscarUsuarios(String nombre, String email, Boolean soloEliminados, Pageable pageable) {
        Specification<Usuario> spec = Specification.where(null);

        if (nombre != null) {
            spec = spec.and(UsuarioSpecifications.nombreContiene(nombre));
        }
        if (email != null) {
            spec = spec.and(UsuarioSpecifications.emailContiene(email));
        }

        if (Boolean.TRUE.equals(soloEliminados)) {
            spec = spec.and((root, query, cb) -> cb.isNotNull(root.get("deletedAt")));
        } else {
            spec = spec.and((root, query, cb) -> cb.isNull(root.get("deletedAt")));
        }

        if (pageable.getSort().isUnsorted()) {
            spec = spec.and(UsuarioSpecifications.ordenPorDisponibilidad());
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        }

        return usuarioRepository.findAll(spec, pageable).map(UsuarioAdminDTO::new);
    }

    @Override
    public Optional<UsuarioAdminDTO> findByIdAdmin(Long id) {
        Optional<UsuarioAdminDTO> encontrado = usuarioRepository.findById(id)
                .map(UsuarioAdminDTO::new);

        if (encontrado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontró un usuario con el ID #" + id + ".")
                    ;
        }

        return encontrado;
    }

    @Override
    public Optional<UsuarioDTO> findById(Long id) {
        Optional<UsuarioDTO> encontrado = usuarioRepository.findById(id)
                .map(UsuarioDTO::new);

        if (encontrado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontró un usuario con el ID: " + id + "."
            );
        }

        return encontrado;
    }

    @Override
    public Optional<UsuarioAdminDTO> findByNombreCompleto(String nombreCompleto) {
        Optional<UsuarioAdminDTO> encontrado = usuarioRepository.findByNombreCompletoContaining(nombreCompleto)
                .map(UsuarioAdminDTO::new);

        if (encontrado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontraron usuarios con el nombre \"" + nombreCompleto + "\"."
            );
        }

        return encontrado;
    }

    @Override
    public Optional<UsuarioAdminDTO> findByEmail(String email) {
        Optional<UsuarioAdminDTO> encontrado = usuarioRepository.findByEmail(email)
                .map(UsuarioAdminDTO::new);

        if (encontrado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontraron usuarios con el email " + email + "."
            );
        }

        return encontrado;
    }

    @Override
    public List<UsuarioAdminDTO> findByRolUsuario(RolUsuario rolUsuario) {
        List<UsuarioAdminDTO> encontrados = usuarioRepository.findByRolUsuario(rolUsuario)
                .stream()
                .map(UsuarioAdminDTO::new).toList();

        if (encontrados.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontraron usuarios con el rol " + rolUsuario + "."
            );
        }
        return encontrados;
    }

    //--------------------------Update--------------------------//
    @Transactional
    @Override
    public Optional<UsuarioDTO> updateUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO, Usuario autenticado) {
        if (!autenticado.getId().equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permiso para modificar este usuario."
            );
        }

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
                        )
                );

        if (usuarioUpdateDTO.getNombreCompleto() != null) {
            usuarioExistente.setNombreCompleto(usuarioUpdateDTO.getNombreCompleto());
        }

        if (usuarioUpdateDTO.getEmail() != null) {

            usuarioRepository.findByEmail(usuarioUpdateDTO.getEmail())
                    .filter(usuario -> !usuario.getId().equals(id))  // <- excluye al mismo usuario
                    .ifPresent(usuario -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ya existe un usuario con el email " + usuarioUpdateDTO.getEmail() + "."
                        );
                    });

            usuarioExistente.setEmail(usuarioUpdateDTO.getEmail());
        }

        if (usuarioUpdateDTO.getTelefono() != null) {

            String telefonoSinCeros = usuarioUpdateDTO.getTelefono().replaceFirst("^0+", "");

            if (telefonoSinCeros.length() < 6) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El teléfono debe tener al menos 6 dígitos."
                );
            }

            // Verifica si el nuevo telefono ya está en uso por otro usuario
            usuarioRepository.findByTelefono(telefonoSinCeros)
                    .filter(usuario -> !usuario.getId().equals(id))
                    .ifPresent(usuario -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ya existe un usuario con el teléfono " + usuarioUpdateDTO.getTelefono() + "."
                        );
                    });
            usuarioExistente.setTelefono(telefonoSinCeros);
        }

        Usuario actualizado = usuarioRepository.save(usuarioExistente);
        return Optional.of(new UsuarioDTO(actualizado));
    }

    @Transactional
    @Override
    public UsuarioDTO updateImagenUsuario(Long id, MultipartFile image, Usuario autenticado) {
        if (!autenticado.getId().equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permiso para modificar la imagen de este usuario."
            );
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
                        )
                );

        imageValidationService.validarImagen(image, TipoValidacion.PERFIL_USUARIO);

        String fotoUrl = cloudinaryService.subirImagen(image, "usuarios");

        usuario.setImagenUrl(fotoUrl);

        usuarioRepository.save(usuario);
        return new UsuarioDTO(usuario);
    }

    @Transactional
    @Override
    public Optional<UsuarioAdminDTO> updateUsuarioAdmin(Long id, UsuarioUpdateRolDTO usuarioUpdateRolDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
                        )
                );

        if (usuarioUpdateRolDTO.getNombreCompleto() != null) {
            usuarioExistente.setNombreCompleto(usuarioUpdateRolDTO.getNombreCompleto());
        }

        if (usuarioUpdateRolDTO.getEmail() != null) {
            usuarioRepository.findByEmail(usuarioUpdateRolDTO.getEmail())
                    .filter(u -> !u.getId().equals(id))
                    .ifPresent(u -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ya existe un usuario con el email " + usuarioUpdateRolDTO.getEmail() + "."
                        );
                    });

            usuarioExistente.setEmail(usuarioUpdateRolDTO.getEmail());
        }

        if (usuarioUpdateRolDTO.getTelefono() != null) {

            String telefonoSinCeros = usuarioUpdateRolDTO.getTelefono().replaceFirst("^0+", "");

            if (telefonoSinCeros.length() < 6) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El teléfono debe tener al menos 6 dígitos."
                );
            }

            usuarioRepository.findByTelefono(telefonoSinCeros)
                    .filter(u -> !u.getId().equals(id))
                    .ifPresent(u -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT, "Ya existe un usuario con el teléfono " + telefonoSinCeros + "."
                        );
                    });

            usuarioExistente.setTelefono(telefonoSinCeros);
        }

        if (usuarioUpdateRolDTO.getRolUsuario() != null) {
            usuarioExistente.setRolUsuario(usuarioUpdateRolDTO.getRolUsuario());
        }

        Usuario actualizado = usuarioRepository.save(usuarioExistente);
        return Optional.of(new UsuarioAdminDTO(actualizado));
    }

    @Transactional
    @Override
    public UsuarioAdminDTO updateImagenUsuarioAdmin(Long id, MultipartFile image) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
                        )
                );

        imageValidationService.validarImagen(image, TipoValidacion.PERFIL_USUARIO);

        String fotoUrl = cloudinaryService.subirImagen(image, "usuarios");

        usuario.setImagenUrl(fotoUrl);

        usuarioRepository.save(usuario);
        return new UsuarioAdminDTO(usuario);
    }

    @Transactional
    @Override
    public UsuarioAdminDTO enableUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
            );
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Este usuario ya está activado."
            );
        }

        usuario.setEnabled(true);
        usuarioRepository.save(usuario);
        return new UsuarioAdminDTO(usuario);
    }

    @Transactional
    @Override
    public UsuarioAdminDTO banUsuario(Long id, boolean forzar) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
            );
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getBannedAt() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Este usuario ya está bloqueado."
            );
        }

        if (!forzar) {
            verificarSiTienePedidosActivos(usuario.getId());
        }

        usuario.setBannedAt(LocalDateTime.now());
        usuarioRepository.save(usuario);

        if (usuario.getRolUsuario() != RolUsuario.ADMIN) {
            cancelarPedidosPendientes(usuario);

            var emprendimientos = emprendimientoRepository.findByUsuarioId(usuario.getId());

            for (var emprendimiento : emprendimientos) {
                if (emprendimiento.getEstaDisponible()) {
                    emprendimiento.setEstaDisponible(false);
                    emprendimientoRepository.save(emprendimiento);
                }
            }
        }

        return new UsuarioAdminDTO(usuario);
    }

    @Transactional
    @Override
    public UsuarioAdminDTO unbanUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
            );
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getBannedAt() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Este usuario no está bloqueado."
            );
        }

        usuario.setBannedAt(null);
        usuarioRepository.save(usuario);
        return new UsuarioAdminDTO(usuario);
    }

    //--------------------------Delete--------------------------//
    @Transactional
    @Override
    public boolean deleteUsuarioAdmin(Long id, boolean forzar) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
                        )
                );

        return procesarEliminacionUsuario(usuario, forzar);
    }

    @Transactional
    @Override
    public boolean deleteUsuario(Long id, Usuario autenticado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
                        )
                );

        if (!autenticado.getId().equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permiso para eliminar este usuario."
            );
        }

        return procesarEliminacionUsuario(usuario, false);
    }

    private boolean procesarEliminacionUsuario(Usuario usuario, boolean forzar) {
        Long id = usuario.getId();

        if (!forzar) {
            verificarSiTienePedidosActivos(id);
        }

        refreshTokenRepository.deleteByUsuario(usuario);

        if (tieneDatosHistoricos(usuario)) {
            realizarBajaLogica(usuario);
            usuarioRepository.save(usuario);
            return true;
        } else {
            usuarioRepository.deleteById(id);
            return true;
        }
    }

    //--------------------------Otros--------------------------//
    @Override
    public Optional<Usuario> findEntityById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    @Override
    public boolean cambiarPasswordAdmin(Long id, String passwordNueva) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
            );
        }

        Usuario usuario = usuarioOpt.get();

        if (passwordEncoder.matches(passwordNueva, usuario.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La nueva contraseña no puede ser igual a la actual."
            );
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        return true;
    }

    @Transactional
    @Override
    public boolean cambiarPassword(Long id, String passwordActual, String passwordNueva, Usuario autenticado) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + id + "."
            );
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getId().equals(autenticado.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permiso para cambiar la contraseña de este usuario."
            );
        }

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "La contraseña actual es incorrecta."
            );
        }

        if (passwordEncoder.matches(passwordNueva, usuario.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La nueva contraseña no puede ser igual a la actual."
            );
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        return true;
    }

    private boolean verificarSiTienePedidos(Long id) {

        // ----------- Pedidos como cliente ----------- //
        boolean tienePedidosComoCliente =
                pedidoRepository.existsByEstadoAndUsuarioId(EstadoPedido.PENDIENTE, id)
                        || pedidoRepository.existsByEstadoAndUsuarioId(EstadoPedido.ACEPTADO, id);

        if (tienePedidosComoCliente) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede eliminar la cuenta mientras tenga pedidos pendientes o aceptados."
            );
        }

        // ----------- Pedidos como dueño de emprendimientos ----------- //
        boolean tienePedidosComoDueno =
                pedidoRepository.existsByEstadoAndEmprendimientoUsuarioId(EstadoPedido.PENDIENTE, id)
                        || pedidoRepository.existsByEstadoAndEmprendimientoUsuarioId(EstadoPedido.ACEPTADO, id);

        if (tienePedidosComoDueno) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede eliminar la cuenta mientras sus emprendimientos tengan pedidos pendientes o aceptados."
            );
        }
        return true;
    }

    private void verificarSiTienePedidosActivos(Long id) {
        Usuario usuarioLogueado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean esAdmin = usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN);

        boolean tienePedidosActivos = pedidoRepository.existsByEstadoAndUsuarioId(EstadoPedido.PENDIENTE, id)
                || pedidoRepository.existsByEstadoAndUsuarioId(EstadoPedido.ACEPTADO, id);

        boolean tienePedidosActivosComoDueno = pedidoRepository.existsByEstadoAndEmprendimientoUsuarioId(EstadoPedido.PENDIENTE, id)
                || pedidoRepository.existsByEstadoAndEmprendimientoUsuarioId(EstadoPedido.ACEPTADO, id);

        if (tienePedidosActivos || tienePedidosActivosComoDueno) {
            String articulo = esAdmin ? "o bloquear la" : "tu";
            String verbo = esAdmin ? "tenga" : "tengas";

            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar " + articulo + " cuenta mientras " + verbo +
                            " pedidos en proceso (pendientes o aceptados).");
        }
    }

    private boolean tieneDatosHistoricos(Usuario usuario) {
        boolean tienePedidosHistoricos = !usuario.getPedidos().isEmpty();
        boolean tieneEmprendimientos = !usuario.getEmprendimientos().isEmpty();

        return tienePedidosHistoricos || tieneEmprendimientos;
    }

    private void realizarBajaLogica(Usuario usuario) {
        var rol = usuario.getRolUsuario();

        if (rol == RolUsuario.DUENO) {
            List<Emprendimiento> emprendimientosCopia = new ArrayList<>(usuario.getEmprendimientos());

            for (Emprendimiento emp : emprendimientosCopia) {
                if (emp.getDeletedAt() != null) {
                    continue;
                }
                boolean tieneHistorialPedidos = pedidoRepository.existsByEmprendimientoId(emp.getId());

                // También cancela los pedidos activos
                emprendimientoService.deleteEmprendimiento(emp.getId(), usuario, true);

                if (!tieneHistorialPedidos) {
                    usuario.getEmprendimientos().remove(emp);
                }
            }
        } else if (rol == RolUsuario.CLIENTE) {
            cancelarPedidosPendientes(usuario);
        }

        String timestamp = String.valueOf(System.currentTimeMillis());

        usuario.setNombreCompleto("usuario_borrado_" + timestamp + "_" + usuario.getNombreCompleto());
        usuario.setEmail("usuario_borrado_" + timestamp + "_" + usuario.getEmail());
        usuario.setTelefono("borrado_" + timestamp + "_" + usuario.getTelefono());
        usuario.setImagenUrl("https://res.cloudinary.com/dsgqbotzi/image/upload/v1767728942/usuario_deleted_gi1u0v.webp");

        usuario.setPassword(passwordEncoder.encode("deleted_user_" + timestamp));
        usuario.setEnabled(false);
        usuario.setDeletedAt(LocalDateTime.now());
    }

    private void cancelarPedidosPendientes(Usuario usuario) {
        List<Pedido> pedidos;
        final boolean esDueno = usuario.getRolUsuario() == RolUsuario.DUENO;

        if (esDueno) { // Solamente al bloquear un dueño
            pedidos = new ArrayList<>();

            for (var emprendimiento : emprendimientoRepository.findByUsuarioId(usuario.getId())) {
                pedidos.addAll(pedidoRepository.findByEmprendimientoId(emprendimiento.getId()));
            }
        }
        else { // Al bloquear o eliminar un cliente
            pedidos = pedidoRepository.findByUsuarioId(usuario.getId());
        }

        for (var pedido : pedidos) {
            if (pedido.getEstado() == EstadoPedido.PENDIENTE) {
                pedido.setEstado(EstadoPedido.CANCELADO);
                pedidoRepository.save(pedido);

                String mensaje =
                        "El pedido #" + pedido.getId() + " fue cancelado porque el " +
                        (esDueno ? "dueño del emprendimiento" : "cliente") +
                        " fue eliminado o bloqueado.";

                notificacionService.createNotificacion(
                        new NotificacionCreateDTO(
                            esDueno ? pedido.getUsuario().getId() : pedido.getEmprendimiento().getUsuario().getId(),
                            pedido.getEmprendimiento().getId(),
                            mensaje,
                            LocalDate.now()
                        )
                );
            }
        }
    }
}
