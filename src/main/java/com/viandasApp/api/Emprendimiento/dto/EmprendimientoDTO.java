package com.viandasApp.api.Emprendimiento.dto;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmprendimientoDTO extends RepresentationModel<EmprendimientoDTO> {
    private Long id;
    private String nombreEmprendimiento;
    private String imagenUrl;
    private String ciudad;
    private String direccion;
    private String telefono;
    private Boolean estaDisponible;
    private UsuarioDTO dueno;

    public EmprendimientoDTO(Emprendimiento emprendimiento){
        this.id = emprendimiento.getId();
        this.imagenUrl = emprendimiento.getImagenUrl();
        this.dueno = new UsuarioDTO(emprendimiento.getUsuario());

        if (emprendimiento.getDeletedAt() != null) {
            this.nombreEmprendimiento = "Emprendimiento Eliminado";
            this.ciudad = "No disponible";
            this.direccion = "No disponible";
            this.telefono = "No disponible";
            this.estaDisponible = false;
        } else {
            this.nombreEmprendimiento = emprendimiento.getNombreEmprendimiento();
            this.ciudad = emprendimiento.getCiudad();
            this.direccion = emprendimiento.getDireccion();
            this.telefono = emprendimiento.getTelefono();
            this.estaDisponible = emprendimiento.getEstaDisponible();
        }
    }
}
