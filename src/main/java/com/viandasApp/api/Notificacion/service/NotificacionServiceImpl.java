package com.viandasApp.api.Notificacion.service;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.service.EmprendimientoServiceImpl;
import com.viandasApp.api.Notificacion.dto.NotificacionCreateDTO;
import com.viandasApp.api.Notificacion.dto.NotificacionDTO;
import com.viandasApp.api.Notificacion.mappers.NotificacionMapper;
import com.viandasApp.api.Notificacion.model.Notificacion;
import com.viandasApp.api.Notificacion.repository.NotificacionRepository;
import com.viandasApp.api.Notificacion.specification.NotificacionSpecifications;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class NotificacionServiceImpl implements NotificacionService{

    private final NotificacionRepository notificacionRepository;
    private final UsuarioServiceImpl usuarioService;
    private final EmprendimientoServiceImpl emprendimientoService;
    private final NotificacionMapper notificacionMapper;

    public NotificacionServiceImpl(NotificacionRepository notificacionRepository,
                                   UsuarioServiceImpl usuarioService,
                                   EmprendimientoServiceImpl emprendimientoService,
                                   NotificacionMapper notificacionMapper) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioService = usuarioService;
        this.emprendimientoService = emprendimientoService;
        this.notificacionMapper = notificacionMapper;
    };

    @Transactional
    @Override
    public NotificacionDTO createNotificacion(NotificacionCreateDTO notificacionCreateDTO) {

        if(notificacionCreateDTO.getDestinatarioId() == null || notificacionCreateDTO.getEmprendimientoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El destinatario y el emprendimiento son obligatorios."
            );
        }

        final long destinatarioId = notificacionCreateDTO.getDestinatarioId();
        Usuario destinatario = usuarioService.findEntityById(destinatarioId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un usuario con ID #" + destinatarioId + "."
                        )
                );

        final long emprendimientoId = notificacionCreateDTO.getEmprendimientoId();
        Emprendimiento emprendimiento = emprendimientoService.findEntityById(emprendimientoId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró un emprendimiento con ID #" + emprendimientoId + "."
                        )
                );

        Notificacion notificacion = notificacionMapper.DTOToEntity(notificacionCreateDTO, destinatario, emprendimiento);

        Notificacion guardada = notificacionRepository.save(notificacion);
        return new NotificacionDTO(guardada);
    }

    @Override
    public Page<NotificacionDTO> buscarNotificaciones(Long destinatarioId, Boolean leida, LocalDate desde, LocalDate hasta, Pageable pageable) {

        Specification<Notificacion> spec = Specification.where(NotificacionSpecifications.byDestinatarioId(destinatarioId));

        if (leida != null) {
            spec = spec.and(NotificacionSpecifications.byLeida(leida));
        }
        if (desde != null) {
            spec = spec.and(NotificacionSpecifications.byFechaDesde(desde));
        }
        if (hasta != null) {
            spec = spec.and(NotificacionSpecifications.byFechaHasta(hasta));
        }

        if (pageable.getSort().isUnsorted()) {
            if (leida == null) {
                spec = spec.and(NotificacionSpecifications.conOrdenamientoDefecto());
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            } else {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "fechaEnviado", "id"));
            }
        }

        return notificacionRepository.findAll(spec, pageable).map(NotificacionDTO::new);
    }

    @Override
    public long contarNoLeidas(Long destinatarioId) {
        return notificacionRepository.countByDestinatarioIdAndLeidaFalse(destinatarioId);
    }

    @Transactional
    @Override
    public boolean deleteNotificacion(Long id) {

        Optional<Notificacion> notificacionEncontrada = notificacionRepository.findById(id);

        if (notificacionEncontrada.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No se encontró una notificación con ID #" + id + "."
            );
        }

        notificacionRepository.deleteById(id);
        return true;
    }

    @Transactional
    @Override
    public NotificacionDTO marcarComoLeida(Long notificacionId, Long destinatarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró una notificación con ID #" + notificacionId + "."
                        )
                );

        if (!notificacion.getDestinatario().getId().equals(destinatarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permiso para modificar esta notificación."
            );
        }

        notificacion.setLeida(true);
        Notificacion guardada = notificacionRepository.save(notificacion);
        return new NotificacionDTO(guardada);
    }
}

