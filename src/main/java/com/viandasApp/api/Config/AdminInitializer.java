package com.viandasApp.api.Config;

import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                Usuario admin = new Usuario(
                        null,
                        "Administrador",
                        "admin@viandas.com",
                        passwordEncoder.encode("admin123"),
                        "12345678",
                        RolUsuario.ADMIN,
                        "https://res.cloudinary.com/dsgqbotzi/image/upload/v1765495736/descarga_rtbq8s.jpg"
                );
                usuarioRepository.save(admin);
                System.out.println("Usuario admin por defecto creado.");
            }
        };
    }
}