package com.viandasApp.api.Usuario.service;

import com.viandasApp.api.Config.SecurityConfig;
import com.viandasApp.api.Emprendimiento.repository.EmprendimientoRepository;
import com.viandasApp.api.Pedido.repository.PedidoRepository;
import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateRolDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import com.viandasApp.api.Vianda.repository.ViandaRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmprendimientoRepository emprendimientoRepository;
    private final ViandaRepository viandaRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, EmprendimientoRepository emprendimientoRepository,
                              ViandaRepository viandaRepository, PedidoRepository pedidoRepository) {
        // Inyección de dependencias a través del constructor
        this.usuarioRepository = usuarioRepository;
        this.emprendimientoRepository = emprendimientoRepository;
        this.viandaRepository = viandaRepository;
        this.pedidoRepository = pedidoRepository;
        this.passwordEncoder = new SecurityConfig().passwordEncoder(); // Obtiene el PasswordEncoder de la configuración de seguridad
    }

    //--------------------------Create--------------------------//
    @Override
    @Transactional
    public UsuarioDTO createUsuario(UsuarioCreateDTO usuarioCreateDTO) {
        Usuario usuario = DTOToEntity(usuarioCreateDTO);

        Usuario usuarioLogueado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Verifica si ya existe un usuario con el mismo email
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el email: " + usuario.getEmail());
        }

        String telefonoSinCeros = usuarioCreateDTO.getTelefono().replaceFirst("^0+", "");
        if (telefonoSinCeros.length() < 7) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El telefono debe tener al menos 7 digitos.");
        }
        usuario.setTelefono(telefonoSinCeros);

        // Verifica si ya existe un usuario con el mismo telefono
        if (usuarioRepository.findByTelefono(usuario.getTelefono()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el telefono: " + usuario.getTelefono());
        }

        // Verifica que el rol del usuario logueado sea ADMIN si el nuevo usuario es ADMIN, de esta manera se evita que un usuario
        // normal pueda registrarse como ADMIN, pero permite que el ADMIN registre nuevos usuarios como ADMIN.
        if (usuarioCreateDTO.getRolUsuario().equals(RolUsuario.ADMIN) && !usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No podes registrarte como ADMIN. Este rol es exclusivo del administrador del sistema.");
        }

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return new UsuarioDTO(savedUsuario);
    }

    //--------------------------Read--------------------------//
    @Override
    public List<UsuarioDTO> readUsuarios() {
        List<UsuarioDTO> encontrados = usuarioRepository.findAll()
                .stream()
                .map(UsuarioDTO::new).toList();

        if (encontrados.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron usuarios");
        }
        return encontrados;
    }

    @Override
    public Optional<UsuarioDTO> findById(Long id) {
        Optional <UsuarioDTO> encontrado = usuarioRepository.findById(id)
                .map(UsuarioDTO::new);

        if (encontrado.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron usuarios con el ID: " + id);
        }

        return encontrado;
    }

    @Override
    public Optional<UsuarioDTO> findByNombreCompleto(String nombreCompleto) {
        Optional <UsuarioDTO> encontrado = usuarioRepository.findByNombreCompletoContaining(nombreCompleto)
                .map(UsuarioDTO::new);

        if (encontrado.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron usuarios con el nombre: " + nombreCompleto);
        }

        return encontrado;
    }

    @Override
    public Optional<UsuarioDTO> findByEmail(String email) {
        Optional <UsuarioDTO> encontrado = usuarioRepository.findByEmail(email)
                .map(UsuarioDTO::new);

        if (encontrado.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron usuarios con el email: " + email);
        }

        return encontrado;
    }

    @Override
    public List<UsuarioDTO> findByRolUsuario(RolUsuario rolUsuario) {
        List<UsuarioDTO> encontrados = usuarioRepository.findByRolUsuario(rolUsuario)
                .stream()
                .map(UsuarioDTO::new).toList();

        if (encontrados.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron usuarios con el rol: " + rolUsuario);
        }
        return encontrados;
    }

    //--------------------------Update--------------------------//
    @Transactional
    @Override
    public Optional<UsuarioDTO> updateUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO, Usuario autenticado) {
        if (!autenticado.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado para actualizar este usuario");
        }

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el usuario con el ID: " + id));

        if (usuarioUpdateDTO.getNombreCompleto() != null) {
            usuarioExistente.setNombreCompleto(usuarioUpdateDTO.getNombreCompleto());
        }

        if (usuarioUpdateDTO.getEmail() != null) {

            if ( usuarioRepository.findByEmail(usuarioUpdateDTO.getEmail()).isPresent() ){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el email: " + usuarioUpdateDTO.getEmail());
            }
            usuarioExistente.setEmail(usuarioUpdateDTO.getEmail());
        }

        if (usuarioUpdateDTO.getTelefono() != null) {

            String telefonoSinCeros = usuarioUpdateDTO.getTelefono().replaceFirst("^0+", "");

            if ( telefonoSinCeros.length()< 7 ){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El telefono debe tener al menos 7 digitos.");
            }

            // Verifica si el nuevo telefono ya está en uso por otro usuario
            if ( usuarioRepository.findByTelefono(telefonoSinCeros).isPresent() ) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el telefono: " + usuarioUpdateDTO.getTelefono());
            }
            usuarioExistente.setTelefono(telefonoSinCeros);
        }

        Usuario actualizado = usuarioRepository.save(usuarioExistente);
        return Optional.of(new UsuarioDTO(actualizado));
    }

    @Transactional
    @Override
    public Optional<UsuarioDTO> updateUsuarioAdmin(Long id, UsuarioUpdateRolDTO usuarioUpdateRolDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el usuario con el ID: " + id));

        if (usuarioUpdateRolDTO.getNombreCompleto() != null) {
            usuarioExistente.setNombreCompleto(usuarioUpdateRolDTO.getNombreCompleto());
        }

        if (usuarioUpdateRolDTO.getEmail() != null) {
            if ( usuarioRepository.findByEmail(usuarioUpdateRolDTO.getEmail()).isPresent() ){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el email: " + usuarioUpdateRolDTO.getEmail());
            }

            usuarioExistente.setEmail(usuarioUpdateRolDTO.getEmail());
        }

        if (usuarioUpdateRolDTO.getTelefono() != null) {

            String telefonoSinCeros = usuarioUpdateRolDTO.getTelefono().replaceFirst("^0+", "");

            if ( telefonoSinCeros.length()< 7 ){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El telefono debe tener al menos 7 digitos.");
            }
            if ( usuarioRepository.findByTelefono(telefonoSinCeros).isPresent() ) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el telefono: " + telefonoSinCeros);
            }

            usuarioExistente.setTelefono(telefonoSinCeros);
        }

        if (usuarioUpdateRolDTO.getRolUsuario() != null) {
            usuarioExistente.setRolUsuario(usuarioUpdateRolDTO.getRolUsuario());
        }

        Usuario actualizado = usuarioRepository.save(usuarioExistente);
        return Optional.of(new UsuarioDTO(actualizado));
    }

    //--------------------------Delete--------------------------//
    @Transactional
    @Override
    public boolean deleteUsuarioAdmin(Long id) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el usuario con el ID: " + id);
        }

        Usuario usuario = usuarioExistente.get();

        usuarioRepository.delete(usuario);
        return true;
    }

    @Transactional
    @Override
    public boolean deleteUsuario(Long id, Usuario autenticado) {

        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el usuario con el ID: " + id);
        }

        if (!autenticado.getId().equals(id)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado para eliminar este usuario");
        }

        Usuario usuario = usuarioExistente.get();

        usuarioRepository.deleteById(id);
        return true;
    }

    //--------------------------Otros--------------------------//
    private Usuario DTOToEntity(UsuarioCreateDTO dto) {
        return new Usuario(
                null, // id será generado por JPA
                dto.getNombreCompleto(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getTelefono(),
                dto.getRolUsuario()
        );
    }

    @Override
    public Optional<Usuario> findEntityById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    @Override
    public boolean cambiarPasswordAdmin(Long id, String passwordNueva) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el usuario con el ID: " + id);
        }

        Usuario usuario = usuarioOpt.get();

        if (passwordEncoder.matches(passwordNueva, usuario.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña no puede ser igual a la actual");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        return true;
    }

    @Transactional
    @Override
    public boolean cambiarPassword(Long id, String passwordActual, String passwordNueva, Usuario autenticado) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el usuario con el ID: " + id);
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getId().equals(autenticado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado para cambiar la contraseña de este usuario");
        }

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La contraseña actual es incorrecta");
        }

        if (passwordEncoder.matches(passwordNueva, usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña no puede ser igual a la actual");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        return true;
    }
}
