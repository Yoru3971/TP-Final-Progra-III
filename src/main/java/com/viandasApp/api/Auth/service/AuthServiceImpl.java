package com.viandasApp.api.Auth.service;

import com.viandasApp.api.Auth.dto.UsuarioLogedResponseDTO;
import com.viandasApp.api.Auth.dto.UsuarioLoginDTO;
import com.viandasApp.api.Auth.dto.UsuarioRegisterDTO;
import com.viandasApp.api.ServiceGenerales.EmailService;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.model.ConfirmacionToken;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.ConfirmacionTokenRepository;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import com.viandasApp.api.Usuario.security.JwtUtil;
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
        emailService.send(
                usuarioRegisterDTO.getEmail(),
                buildEmail(usuarioRegisterDTO.getNombreCompleto(), link)
        );
        return new UsuarioDTO(savedUsuario);
    }

    // === CONFIRMACION TOKEN ===
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
    // === REENVIAR TOKEN VALIDACION ===
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
        emailService.send(
                usuario.getEmail(),
                buildEmail(usuario.getNombreCompleto(), link)
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

    // === ESTRUCTURA MAIL ===
    private String buildEmail(String nombre, String link) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Verifica tu correo</title>\n" +
                "    <style>\n" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }\n" +
                "        .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); overflow: hidden; }\n" +
                "        .header { background-color: #2c3e50; padding: 20px; text-align: center; color: #ffffff; }\n" +
                "        .header h1 { margin: 0; font-size: 24px; }\n" +
                "        .content { padding: 30px; color: #333333; line-height: 1.6; }\n" +
                "        .button-container { text-align: center; margin: 30px 0; }\n" +
                "        .button { background-color: #27ae60; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block; }\n" +
                "        .footer { background-color: #f9f9f9; padding: 15px; text-align: center; font-size: 12px; color: #777777; border-top: 1px solid #eeeeee; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>¡Bienvenido a ViandasApp!</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Hola <strong>" + nombre + "</strong>,</p>\n" +
                "            <p>Gracias por registrarte. Estás a un solo paso de empezar a disfrutar de las mejores viandas.</p>\n" +
                "            <p>Por favor, valida tu correo electrónico haciendo clic en el siguiente botón:</p>\n" +
                "            \n" +
                "            <div class=\"button-container\">\n" +
                "                <a href=\"" + link + "\" class=\"button\">Activar mi cuenta</a>\n" +
                "            </div>\n" +
                "            \n" +
                "            <p>Si no creaste esta cuenta, puedes ignorar este mensaje.</p>\n" +
                "            <p>El enlace expirará en 15 minutos.</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            &copy; 2025 ViandasApp. Todos los derechos reservados.\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}



