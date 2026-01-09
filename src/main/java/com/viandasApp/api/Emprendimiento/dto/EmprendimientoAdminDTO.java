package com.viandasApp.api.Emprendimiento.dto;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EmprendimientoAdminDTO extends RepresentationModel<EmprendimientoAdminDTO> {
    private Long id;
    private String nombreEmprendimiento;
    private String imagenUrl;
    private String ciudad;
    private String direccion;
    private String telefono;
    private Boolean estaDisponible;
    private UsuarioDTO dueno;

    private LocalDateTime fechaEliminacion;

    public EmprendimientoAdminDTO(Emprendimiento emprendimiento) {
        this.id = emprendimiento.getId();
        this.nombreEmprendimiento = emprendimiento.getNombreEmprendimiento();
        this.imagenUrl = emprendimiento.getImagenUrl();
        this.ciudad = emprendimiento.getCiudad();
        this.direccion = emprendimiento.getDireccion();
        this.telefono = emprendimiento.getTelefono();
        this.estaDisponible = emprendimiento.getEstaDisponible();
        this.dueno = new UsuarioDTO(emprendimiento.getUsuario());

        this.fechaEliminacion = emprendimiento.getDeletedAt();
    }
}
