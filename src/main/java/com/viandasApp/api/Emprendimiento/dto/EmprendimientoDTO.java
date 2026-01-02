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
        this.nombreEmprendimiento = emprendimiento.getNombreEmprendimiento();
        this.ciudad = emprendimiento.getCiudad();
        this.direccion = emprendimiento.getDireccion();
        this.telefono = emprendimiento.getTelefono();
        this.estaDisponible = emprendimiento.getEstaDisponible();
        this.dueno = new UsuarioDTO(emprendimiento.getUsuario());
        this.imagenUrl = emprendimiento.getImagenUrl();
    }
}
