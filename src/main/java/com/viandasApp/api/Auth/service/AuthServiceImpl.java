package com.viandasApp.api.Auth.service;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.viandasApp.api.Auth.dto.UsuarioLogedResponseDTO;
import com.viandasApp.api.Auth.dto.UsuarioLoginDTO;
import com.viandasApp.api.Auth.dto.UsuarioRegisterDTO;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import com.viandasApp.api.Usuario.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public UsuarioDTO registerUsuario(UsuarioRegisterDTO usuarioRegisterDTO) {
        Usuario usuario = DTOToEntity(usuarioRegisterDTO);

        // Verifica si ya existe un usuario con el mismo email
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el email: " + usuario.getEmail());
        }

        String telefonoSinCeros = usuarioRegisterDTO.getTelefono().replaceFirst("^0+", "");
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
        if (usuarioRegisterDTO.getRolUsuario().equals(RolUsuario.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No podes registrarte como ADMIN. Este rol es exclusivo del administrador del sistema.");
        }

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return new UsuarioDTO(savedUsuario);
    }


    @Override
    @Transactional
    public UsuarioLogedResponseDTO loginUsuario(UsuarioLoginDTO usuarioLoginDTO) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            usuarioLoginDTO.getEmail(),
                            usuarioLoginDTO.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email o contraseña incorrecta.");
        }

        Optional<Usuario> usuario = usuarioRepository.findByEmail(usuarioLoginDTO.getEmail());

        if (!usuarioRepository.findByEmail(usuario.get().getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se encontro ningun usuario con el mail: " + usuario.get().getEmail());
        }

        String roleName = usuario.get().getRolUsuario().name();
        String token = jwtUtil.generateToken(usuario.get().getEmail(), roleName);

        return new UsuarioLogedResponseDTO(usuario.get().getId(), token);
    }

    private Usuario DTOToEntity(UsuarioRegisterDTO dto) {

        return new Usuario(
                null, // id será generado por JPA
                dto.getNombreCompleto(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getTelefono(),
                dto.getRolUsuario()
        );
    }

}



