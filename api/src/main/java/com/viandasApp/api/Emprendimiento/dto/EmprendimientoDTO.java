package com.viandasApp.api.Emprendimiento.dto;


import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmprendimientoDTO {
    private Long id;

    private String nombreEmprendimiento;

    private String ciudad;

    private String direccion;

    private String telefono;

    private UsuarioDTO dueno;

    public EmprendimientoDTO(Emprendimiento emprendimiento){
        this.id = emprendimiento.getId();
        this.nombreEmprendimiento = emprendimiento.getNombreEmprendimiento();
        this.ciudad = emprendimiento.getCiudad();
        this.direccion = emprendimiento.getDireccion();
        this.telefono = emprendimiento.getTelefono();
        this.dueno = new UsuarioDTO(emprendimiento.getUsuario());
    }
}
