package com.viandasApp.api.Reclamo.dto;

import com.viandasApp.api.Reclamo.model.CategoriaReclamo;
import com.viandasApp.api.Reclamo.model.EstadoReclamo;
import com.viandasApp.api.Reclamo.model.Reclamo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReclamoDTO extends RepresentationModel<ReclamoDTO> {
    private Long id;
    private String codigoTicket;
    private String emailUsuario;
    private CategoriaReclamo categoria;
    private String descripcion;
    private EstadoReclamo estado;
    private String respuestaAdmin;
    private LocalDateTime fechaCreacion;

    public ReclamoDTO(Reclamo reclamo) {
        this.id = reclamo.getId();
        this.codigoTicket = reclamo.getCodigoTicket();
        this.emailUsuario = reclamo.getEmailUsuario();
        this.categoria = reclamo.getCategoria();
        this.descripcion = reclamo.getDescripcion();
        this.estado = reclamo.getEstado();
        this.respuestaAdmin = reclamo.getRespuestaAdmin();
        this.fechaCreacion = reclamo.getFechaCreacion();
    }
}
