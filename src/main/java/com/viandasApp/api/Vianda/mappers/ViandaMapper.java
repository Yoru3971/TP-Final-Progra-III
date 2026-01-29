package com.viandasApp.api.Vianda.mappers;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Vianda.dto.ViandaCreateDTO;
import com.viandasApp.api.Vianda.model.Vianda;
import org.springframework.stereotype.Component;

@Component
public class ViandaMapper {

    public Vianda DTOToEntity(ViandaCreateDTO dto, String fotoUrl, Emprendimiento emprendimiento) {
        return new Vianda(
                dto.getNombreVianda(),
                dto.getCategoria(),
                dto.getDescripcion(),
                dto.getPrecio(),
                dto.getEsVegano(),
                dto.getEsVegetariano(),
                dto.getEsSinTacc(),
                emprendimiento,
                fotoUrl
        );
    }
}
