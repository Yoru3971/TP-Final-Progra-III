package com.viandasApp.api.Vianda.dto;

import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.model.Vianda;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ViandaAdminDTO extends RepresentationModel<ViandaAdminDTO> {
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

    private LocalDateTime fechaEliminacion;

    public ViandaAdminDTO(Vianda vianda) {
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
        this.imagenUrl = vianda.getImagenUrl();
        this.fechaEliminacion = vianda.getDeletedAt();
    }
}
