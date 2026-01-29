package com.viandasApp.api.Emprendimiento.mappers;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class EmprendimientoMapper {

    public Emprendimiento DTOToEntity(CreateEmprendimientoDTO dto, String imageUrl, Usuario usuarioPropietario) {

        return new Emprendimiento(
                dto.getNombreEmprendimiento(),
                dto.getCiudad(),
                dto.getDireccion(),
                dto.getTelefono(),
                usuarioPropietario,
                imageUrl
        );
    }
}
