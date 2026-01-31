package com.viandasApp.api.Reclamo.service;

import com.viandasApp.api.Reclamo.dto.ReclamoRequestDTO;
import com.viandasApp.api.Reclamo.model.EstadoReclamo;
import com.viandasApp.api.Reclamo.model.Reclamo;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReclamoService {
    Reclamo crearReclamo(ReclamoRequestDTO dto);
    Page<Reclamo> buscarReclamos(Usuario usuario, EstadoReclamo estado, String emailFiltro, Pageable pageable);
    List<Reclamo> listarReclamosPorUsuario(String email);
    List<Reclamo> listarTodosLosReclamos();
    Optional<Reclamo> obtenerReclamoPorId(Long id);
    List<Reclamo> listarReclamosPorEstado(EstadoReclamo estado);
    Reclamo actualizarEstadoReclamo(Long id, EstadoReclamo nuevoEstado, String respuestaAdmin);
}
