package com.viandasApp.api.Usuario.repository;

import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreCompletoContaining(String nombreCompleto);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRolUsuario(RolUsuario rolUsuario);
    Optional<Usuario> findByTelefono(String telefono);
    void deleteByEnabledFalseAndCreatedAtBefore(LocalDateTime fechaLimite);
}
