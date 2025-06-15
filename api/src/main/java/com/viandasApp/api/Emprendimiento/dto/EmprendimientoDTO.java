package com.viandasApp.api.Emprendimiento.dto;


import com.viandasApp.api.Usuario.model.Usuario;
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

    private Usuario usuario;
}
