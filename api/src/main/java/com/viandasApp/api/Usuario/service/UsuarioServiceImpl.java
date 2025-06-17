package com.viandasApp.api.Usuario.service;

import com.viandasApp.api.Config.SecurityConfig;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.repository.EmprendimientoRepository;
import com.viandasApp.api.Pedido.model.Pedido;
import com.viandasApp.api.Pedido.repository.PedidoRepository;
import com.viandasApp.api.Usuario.dto.UsuarioCreateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateDTO;
import com.viandasApp.api.Usuario.dto.UsuarioUpdateRolDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import com.viandasApp.api.Vianda.model.Vianda;
import com.viandasApp.api.Vianda.repository.ViandaRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Transactional
    @Override
    public UsuarioDTO createUsuario(UsuarioCreateDTO usuarioCreateDTO) {
        Usuario usuario = DTOToEntity(usuarioCreateDTO);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return new UsuarioDTO(savedUsuario);
    }

    @Override
    public List<UsuarioDTO> readUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioDTO> findById(Long id) {
        return usuarioRepository.findById(id).map(UsuarioDTO::new);
    }

    @Override
    public Optional<Usuario> findEntityById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<UsuarioDTO> findByNombreCompleto(String nombreCompleto) {
        return usuarioRepository.findByNombreCompletoContaining(nombreCompleto).map(UsuarioDTO::new);
    }

    @Override
    public Optional<UsuarioDTO> findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(UsuarioDTO::new);
    }

    @Override
    public List<UsuarioDTO> findByRolUsuario(RolUsuario rolUsuario) {
        return usuarioRepository.findByRolUsuario(rolUsuario)
                .stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Optional<UsuarioDTO> updateUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO, Usuario autenticado) {
        if (!autenticado.getId().equals(id)) {
            return Optional.empty(); // acceso denegado
        }

        return usuarioRepository.findById(id).map(usuarioExistente -> {

            if (usuarioUpdateDTO.getNombreCompleto() != null) {
                usuarioExistente.setNombreCompleto(usuarioUpdateDTO.getNombreCompleto());
            }

            if (usuarioUpdateDTO.getEmail() != null) {
                usuarioExistente.setEmail(usuarioUpdateDTO.getEmail());
            }

            if (usuarioUpdateDTO.getTelefono() != null) {
                usuarioExistente.setTelefono(usuarioUpdateDTO.getTelefono());
            }

            Usuario actualizado = usuarioRepository.save(usuarioExistente);
            return new UsuarioDTO(actualizado);
        });
    }

    @Transactional
    @Override
    public Optional<UsuarioDTO> updateUsuarioAdmin(Long id, UsuarioUpdateRolDTO usuarioUpdateRolDTO) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {

            if (usuarioUpdateRolDTO.getNombreCompleto() != null) {
                usuarioExistente.setNombreCompleto(usuarioUpdateRolDTO.getNombreCompleto());
            }

            if (usuarioUpdateRolDTO.getEmail() != null) {
                usuarioExistente.setEmail(usuarioUpdateRolDTO.getEmail());
            }

            if (usuarioUpdateRolDTO.getTelefono() != null) {
                usuarioExistente.setTelefono(usuarioUpdateRolDTO.getTelefono());
            }

            if (usuarioUpdateRolDTO.getRolUsuario() != null) {
                usuarioExistente.setRolUsuario(usuarioUpdateRolDTO.getRolUsuario());
            }

            Usuario actualizado = usuarioRepository.save(usuarioExistente);
            return new UsuarioDTO(actualizado);
        });
    }

    @Transactional
    @Override
    public boolean deleteUsuarioAdmin(Long id) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isEmpty()) return false;

        Usuario usuario = usuarioExistente.get();

        usuarioRepository.delete(usuario);
        return true;
    }

    @Transactional
    @Override
    public boolean deleteUsuario(Long id, Usuario autenticado) {
        if (!autenticado.getId().equals(id)){
            return false;
        }
        usuarioRepository.deleteById(id);
        return true;
    }

    private Usuario DTOToEntity(UsuarioCreateDTO usuarioCreateDTO) {
        return new Usuario(
                usuarioCreateDTO.getId(),
                usuarioCreateDTO.getNombreCompleto(),
                usuarioCreateDTO.getEmail(),
                passwordEncoder.encode(usuarioCreateDTO.getPassword()),
                usuarioCreateDTO.getTelefono(),
                usuarioCreateDTO.getRolUsuario()
        );
    }

    @Override
    public boolean cambiarPasswordAdmin(Long id, String passwordNueva) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()){
            return false;
        }

        Usuario usuario = usuarioOpt.get();

        if (passwordEncoder.matches(passwordNueva, usuario.getPassword())){
            return false;
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        return true;
    }

    @Override
    public boolean cambiarPassword(Long id, String passwordActual, String passwordNueva, Usuario autenticado) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) return false;
        Usuario usuario = usuarioOpt.get();

        if (!usuario.getId().equals(autenticado.getId())) {
            return false;
        }

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            return false;
        }

        if (passwordEncoder.matches(passwordNueva, usuario.getPassword())) {
            return false;
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        return true;
    }
}
