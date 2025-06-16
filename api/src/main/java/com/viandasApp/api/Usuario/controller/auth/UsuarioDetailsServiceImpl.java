package com.viandasApp.api.Usuario.controller.auth;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        //Para ver si el usuario existe y está autenticado correctamente, es solo para testeo
        System.out.println("Autenticando a: " + email);
        System.out.println("Password en la base de datos: " + usuario.getPassword());

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword()) // contraseña hasheada
                .roles(usuario.getRolUsuario().name())
                .build();
    }
}
