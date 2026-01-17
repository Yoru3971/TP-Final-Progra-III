package com.viandasApp.api.Vianda.repository;

import com.viandasApp.api.Vianda.model.CategoriaVianda;
import com.viandasApp.api.Vianda.model.Vianda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViandaRepository extends JpaRepository<Vianda, Long>, JpaSpecificationExecutor<Vianda> {

    /*
       Querys personalizadas para obtener las categorías de viandas de un emprendimiento (varía según rol)
            ADMIN: devuelve todas las categorías (viandas disponibles, no disponibles y eliminadas)
            DUEÑO: devuelve categorías de viandas disponibles y no disponibles
            PUBLIC/CLIENTE: devuelve categorías de viandas disponibles
     */

    @Query("SELECT DISTINCT v.categoria FROM Vianda v WHERE v.emprendimiento.id = :idEmprendimiento")
    List<CategoriaVianda> findCategoriasByEmprendimientoIdAdmin(@Param("idEmprendimiento") Long idEmprendimiento);

    @Query("SELECT DISTINCT v.categoria FROM Vianda v WHERE v.emprendimiento.id = :idEmprendimiento AND v.deletedAt IS NULL")
    List<CategoriaVianda> findCategoriasByEmprendimientoIdOwner(@Param("idEmprendimiento") Long idEmprendimiento);

    @Query("SELECT DISTINCT v.categoria FROM Vianda v WHERE v.emprendimiento.id = :idEmprendimiento AND v.estaDisponible = true AND v.deletedAt IS NULL")
    List<CategoriaVianda> findCategoriasByEmprendimientoIdPublic(@Param("idEmprendimiento") Long idEmprendimiento);

}
