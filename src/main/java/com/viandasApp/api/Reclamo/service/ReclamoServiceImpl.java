package com.viandasApp.api.Reclamo.service;

import com.viandasApp.api.Reclamo.dto.ReclamoRequestDTO;
import com.viandasApp.api.Reclamo.model.EstadoReclamo;
import com.viandasApp.api.Reclamo.model.Reclamo;
import com.viandasApp.api.Reclamo.repository.ReclamoRepository;
import com.viandasApp.api.ServiceGenerales.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReclamoServiceImpl implements ReclamoService {
    private final ReclamoRepository reclamoRepository;
    private final EmailService emailService;

    @Transactional
    @Override
    public Reclamo crearReclamo(ReclamoRequestDTO dto) {
        String ticketCode = "TCK-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Reclamo reclamo = Reclamo.builder()
                .codigoTicket(ticketCode)
                .emailUsuario(dto.getEmail())
                .categoria(dto.getCategoria())
                .descripcion(dto.getDescripcion())
                .estado(EstadoReclamo.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Reclamo saved = reclamoRepository.save(reclamo);

        // Enviar notificación por mail al USUARIO
        emailService.sendReclamoConfirmacion(dto.getEmail(), "Usuario", ticketCode);

        // Enviar notificación por mail al ADMIN (mail del sistema)
        emailService.sendReclamoNotificacionAdmin(ticketCode, dto.getCategoria().name(), dto.getDescripcion(), dto.getEmail());

        return saved;
    }

    @Override
    public List<Reclamo> listarReclamosPorUsuario(String email) {
        return reclamoRepository.findByEmailUsuarioOrderByFechaCreacion(email);
    }

    //Metodos Admin
    @Override
    public List<Reclamo> listarTodosLosReclamos() {
        return reclamoRepository.findAllByOrderByFechaCreacionDesc();
    }

    //REVISAR IMPLEMENTACION, CAPAZ DEBERIA SER POR N°TICKET IDK
    @Override
    public Optional<Reclamo> obtenerReclamoPorId(Long id) {
        return reclamoRepository.findById(id);
    }

    @Override
    public List<Reclamo> listarReclamosPorEstado(EstadoReclamo estado) {
        return reclamoRepository.findByEstadoOrderByFechaCreacionDesc(estado);
    }

    @Transactional
    @Override
    public Reclamo actualizarEstadoReclamo(Long id, EstadoReclamo nuevoEstado, String respuestaAdmin) {
        Reclamo reclamo = reclamoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamo no encontrado"));

        reclamo.setEstado(nuevoEstado);

        if (respuestaAdmin != null && !respuestaAdmin.isBlank()) {
            reclamo.setRespuestaAdmin(respuestaAdmin);
        }

        Reclamo updated = reclamoRepository.save(reclamo);

        // Notificar al usuario
        // Nota: Podrías buscar el nombre del usuario si quisieras, aquí pongo "Usuario" genérico o el mail
        emailService.sendCambioEstadoReclamo(reclamo.getEmailUsuario(), "Usuario", reclamo.getCodigoTicket(), nuevoEstado.name(), respuestaAdmin);

        return updated;
    }
}
