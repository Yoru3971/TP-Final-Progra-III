package com.viandasApp.api.Vianda.dto;


import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.model.Vianda;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViandaDTO {
    private Long id;

    private String nombreVianda;

    private CategoriaVianda categoria;

    private String descripcion;

    private Double precio;

    private Boolean esVegano;

    private Boolean esVegetariano;

    private Boolean esSinTacc;

    private Long emprendimientoId;

    private Boolean estaDisponible;

    public ViandaDTO(Vianda vianda) {
        this.id = vianda.getId();
        this.nombreVianda = vianda.getNombreVianda();
        this.categoria = vianda.getCategoria();
        this.descripcion = vianda.getDescripcion();
        this.precio = vianda.getPrecio();
        this.esVegano = vianda.getEsVegano();
        this.esVegetariano = vianda.getEsVegetariano();
        this.esSinTacc = vianda.getEsSinTacc();
        this.emprendimientoId = vianda.getEmprendimiento().getId();
        this.estaDisponible = vianda.getEstaDisponible();
    }
}
