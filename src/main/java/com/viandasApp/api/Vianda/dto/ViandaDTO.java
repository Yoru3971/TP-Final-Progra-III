package com.viandasApp.api.Vianda.dto;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.model.Vianda;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViandaDTO extends RepresentationModel<ViandaDTO> {
    private Long id;
    private String nombreVianda;
    private CategoriaVianda categoria;
    private String descripcion;
    private Double precio;
    private Boolean esVegano;
    private Boolean esVegetariano;
    private Boolean esSinTacc;
    private EmprendimientoDTO emprendimiento;
    private Boolean estaDisponible;
    private String imagenUrl;

    public ViandaDTO(Vianda vianda) {
        this.id = vianda.getId();
        this.imagenUrl = vianda.getImagenUrl();
        this.emprendimiento = new EmprendimientoDTO(vianda.getEmprendimiento());

        if (vianda.getDeletedAt() != null) {
            this.nombreVianda = "Vianda Eliminada";
            this.descripcion = "No disponible";
            this.categoria = null;
            this.precio = 0.0;
            this.esVegano = false;
            this.esVegetariano = false;
            this.esSinTacc = false;
            this.estaDisponible = false;
        } else {
            this.nombreVianda = vianda.getNombreVianda();
            this.categoria = vianda.getCategoria();
            this.descripcion = vianda.getDescripcion();
            this.precio = vianda.getPrecio();
            this.esVegano = vianda.getEsVegano();
            this.esVegetariano = vianda.getEsVegetariano();
            this.esSinTacc = vianda.getEsSinTacc();
            this.estaDisponible = vianda.getEstaDisponible();
        }
    }
}
