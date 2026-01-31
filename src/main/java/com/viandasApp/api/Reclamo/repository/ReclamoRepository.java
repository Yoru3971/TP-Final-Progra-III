package com.viandasApp.api.Reclamo.repository;

import com.viandasApp.api.Reclamo.model.EstadoReclamo;
import com.viandasApp.api.Reclamo.model.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Long>, JpaSpecificationExecutor<Reclamo> {
    Optional<Reclamo> findByCodigoTicket(String codigoTicket);
    List<Reclamo> findByEmailUsuarioOrderByFechaCreacion(String emailUsuario);
    //Para Admin
    List<Reclamo> findAllByOrderByFechaCreacionDesc();
    List<Reclamo> findByEstadoOrderByFechaCreacionDesc(EstadoReclamo estado);
}
