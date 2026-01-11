package com.viandasApp.api.Reclamo.service;

import com.viandasApp.api.Reclamo.dto.ReclamoRequestDTO;
import com.viandasApp.api.Reclamo.model.EstadoReclamo;
import com.viandasApp.api.Reclamo.model.Reclamo;

import java.util.List;
import java.util.Optional;

public interface ReclamoService {
    Reclamo crearReclamo(ReclamoRequestDTO dto);
    List<Reclamo> listarReclamosPorUsuario(String email);
    //Para Admin
    List<Reclamo> listarTodosLosReclamos();
    Optional<Reclamo> obtenerReclamoPorId(Long id);
    List<Reclamo> listarReclamosPorEstado(EstadoReclamo estado);
    Reclamo actualizarEstadoReclamo(Long id, EstadoReclamo nuevoEstado, String respuestaAdmin);
}
