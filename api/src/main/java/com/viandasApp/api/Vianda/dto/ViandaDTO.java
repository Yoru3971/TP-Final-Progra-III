package com.viandasApp.api.Vianda.dto;


import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
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

    private EmprendimientoDTO emprendimiento;

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
        this.emprendimiento = new EmprendimientoDTO(vianda.getEmprendimiento());
        this.estaDisponible = vianda.getEstaDisponible();
    }
}
