package com.viandasApp.api.Vianda.repository;

import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.model.Vianda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViandaRepository extends JpaRepository<Vianda, Long> {
    List<Vianda> findByCategoria(CategoriaVianda categoriaVianda);
    List<Vianda> findByNombreViandaContainingIgnoreCase(String nombre);
    List<Vianda> findByEmprendimientoId(Long id);
    List<Vianda> findByPrecioBetween(Double min, Double max);
    List<Vianda> findByEsVeganoTrue();
    List<Vianda> findByEsVegetarianoTrue();
    List<Vianda> findByEsSinTaccTrue();
}
