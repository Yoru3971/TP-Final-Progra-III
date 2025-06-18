package com.viandasApp.api.Notificacion.repository;


import com.viandasApp.api.Notificacion.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findAllByDestinatarioId(Long destinatarioId);
    List<Notificacion> findAllByEmprendimientoId(Long emprendimientoId);
    List<Notificacion> findAllByFechaEnviadoBetween(LocalDate start, LocalDate end);
    List<Notificacion> findAllByDestinatarioIdAndFechaEnviadoBetween(Long destinatarioId, LocalDate start, LocalDate end);
}
