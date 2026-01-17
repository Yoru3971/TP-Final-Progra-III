package com.viandasApp.api.Auth.service;

import com.viandasApp.api.Auth.dto.UsuarioLogedResponseDTO;
import com.viandasApp.api.Auth.dto.UsuarioLoginDTO;
import com.viandasApp.api.Auth.dto.UsuarioRegisterDTO;
import com.viandasApp.api.ServiceGenerales.EmailService;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Auth.model.ConfirmacionToken;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Auth.repository.ConfirmacionTokenRepository;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import com.viandasApp.api.Security.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ConfirmacionTokenRepository confirmacionTokenRepository;
    private final EmailService emailService;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                           ConfirmacionTokenRepository confirmacionTokenRepository, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.confirmacionTokenRepository = confirmacionTokenRepository;
        this.emailService = emailService;
    }

    // === REGISTRO USUARIO ===
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

        //logica token
        String token = UUID.randomUUID().toString();
        ConfirmacionToken confirmationToken = new ConfirmacionToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), // Expira en 15 min
                savedUsuario
        );
        confirmacionTokenRepository.save(confirmationToken);

        //enviar mail
        String link = "http://localhost:4200/confirmar-cuenta?token=" + token;
        emailService.sendValidacionCuenta(
                usuarioRegisterDTO.getEmail(),
                usuarioRegisterDTO.getNombreCompleto(),
                link
        );
        return new UsuarioDTO(savedUsuario);
    }

    // === CONFIRMACION CUENTA CON TOKEN EMAIL ===
    @Override
    @Transactional
    public String confirmToken(String token) {
        ConfirmacionToken confirmationToken = confirmacionTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token no encontrado"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya fue confirmado");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token expiró");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());

        Usuario usuario = confirmationToken.getUsuario();
        usuario.setEnabled(true);
        usuarioRepository.save(usuario);

        return "Cuenta confirmada exitosamente";
    }

    // === LOGIN ===
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
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cuenta no validada. Por favor revisa tu email.");
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrecta.");
        }

        Optional<Usuario> usuario = usuarioRepository.findByEmail(usuarioLoginDTO.getEmail());

        String roleName = usuario.get().getRolUsuario().name();
        String token = jwtUtil.generateToken(usuario.get().getEmail(), roleName);

        return new UsuarioLogedResponseDTO(usuario.get().getId(), token);
    }
    // === REENVIAR TOKEN VALIDACION DE CUENTA EMAIL===
    @Override
    @Transactional
    public String resendToken(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe"));

        if (usuario.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta cuenta ya está verificada. Puedes iniciar sesión.");
        }

        String token = UUID.randomUUID().toString();
        ConfirmacionToken confirmationToken = new ConfirmacionToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                usuario
        );
        confirmacionTokenRepository.save(confirmationToken);

        String link = "http://localhost:4200/confirmar-cuenta?token=" + token;
        emailService.sendValidacionCuenta(
                usuario.getEmail(),
                usuario.getNombreCompleto(),
                link
        );

        return "Nuevo código de verificación enviado.";
    }

    private Usuario DTOToEntity(UsuarioRegisterDTO dto) {

        String imagenPorDefecto = "https://res.cloudinary.com/dsgqbotzi/image/upload/v1765496442/usuario_por_defecto_dtac7c.jpg";

        Usuario u = new Usuario(
                null,
                dto.getNombreCompleto(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getTelefono(),
                dto.getRolUsuario(),
                imagenPorDefecto
        );
        u.setEnabled(false);
        return u;
    }

}



