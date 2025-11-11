package com.viandasApp.api.Notificacion.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.dto.NotificacionDTO;
import com.viandasApp.api.Notificacion.model.Notificacion;
import com.viandasApp.api.Notificacion.repository.NotificacionRepository;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class NotificacionServiceImpl implements NotificacionService{

    private final NotificacionRepository notificacionRepository;
    private final UsuarioServiceImpl usuarioService;
    private final EmprendimientoServiceImpl emprendimientoService;

    public NotificacionServiceImpl(NotificacionRepository notificacionRepository,
                                   UsuarioServiceImpl usuarioService,
                                   EmprendimientoServiceImpl emprendimientoService) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioService = usuarioService;
        this.emprendimientoService = emprendimientoService;
    };

    @Transactional
    @Override
    public NotificacionDTO createNotificacion(NotificacionCreateDTO notificacionCreateDTO) {

        if(notificacionCreateDTO.getDestinatarioId() == null || notificacionCreateDTO.getEmprendimientoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El destinatario y el emprendimiento son obligatorios");
        }

        Usuario destinatario = usuarioService.findEntityById(notificacionCreateDTO.getDestinatarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(notificacionCreateDTO.getEmprendimientoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado"));

        Notificacion notificacion = DTOtoEntity(notificacionCreateDTO);

        Notificacion guardada = notificacionRepository.save(notificacion);
        return new NotificacionDTO(guardada);
    }

    @Override
    public List<NotificacionDTO> getAllNotificaciones() {

        List<Notificacion> notificaciones = notificacionRepository.findAll();

        if (notificaciones.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron notificaciones");
        }
        return notificaciones.stream().map(NotificacionDTO::new).toList();
    }

    @Override
    public List<NotificacionDTO> getAllByDestinatarioId(Long destinatarioId) {

        Usuario usuario = usuarioService.findEntityById(destinatarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<Notificacion> notificaciones = notificacionRepository.findAllByDestinatarioId(destinatarioId);

        if (notificaciones.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron notificaciones para el destinatario");
        }
        return notificaciones.stream().map(NotificacionDTO::new).toList();
    }

    @Override
    public List<NotificacionDTO> getAllByEmprendimientoId(Long emprendimientoId) {

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(emprendimientoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado"));

        List<Notificacion> notificaciones = notificacionRepository.findAllByEmprendimientoId(emprendimientoId);
        if (notificaciones.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron notificaciones para el emprendimiento");
        }
        return notificaciones.stream().map(NotificacionDTO::new).toList();
    }

    @Override
    public List<NotificacionDTO> getAllByFechaEnviadoBetween(LocalDate start, LocalDate end) {

        if (start == null || end == null || start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rango de fechas inválido");
        }

        List<Notificacion> notificaciones = notificacionRepository.findAllByFechaEnviadoBetween(start, end);

        if (notificaciones.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron notificaciones en el rango de fechas especificado");
        }
        return notificaciones.stream().map(NotificacionDTO::new).toList();
    }

    @Override
    public List<NotificacionDTO> getAllByFechaEnviadoBetweenAndDestinatarioId(Long destinatarioId, LocalDate start, LocalDate end) {

        Usuario usuario = usuarioService.findEntityById(destinatarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (start == null || end == null || start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rango de fechas inválido");
        }

        List<Notificacion> notificaciones = notificacionRepository.findAllByDestinatarioIdAndFechaEnviadoBetween(destinatarioId,start, end);
        if (notificaciones.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron notificaciones en el rango de fechas especificado");
        }

        return notificaciones.stream().map(NotificacionDTO::new).toList();
    }

    @Transactional
    @Override
    public boolean deleteNotificacion(Long id) {

        Optional<Notificacion> notificacionEncontrada = notificacionRepository.findById(id);

        if (notificacionEncontrada.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificación no encontrada");
        }

        notificacionRepository.deleteById(id);
        return true;
    }

    private Notificacion DTOtoEntity(NotificacionCreateDTO notificacionCreateDTO) {

        Notificacion notificacion = new Notificacion();

        Usuario usuarioEncontrado = usuarioService.findEntityById(notificacionCreateDTO.getDestinatarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Emprendimiento emprendimiento = emprendimientoService.findEntityById(notificacionCreateDTO.getEmprendimientoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado"));

        notificacion.setDestinatario(usuarioEncontrado);
        notificacion.setEmprendimiento(emprendimiento);
        notificacion.setMensaje(notificacionCreateDTO.getMensaje());
        notificacion.setFechaEnviado(notificacionCreateDTO.getFechaEnviado());

        return notificacion;
    }
}

