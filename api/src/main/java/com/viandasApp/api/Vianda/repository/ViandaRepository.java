package com.viandasApp.api.Vianda.repository;

import com.viandasApp.api.Vianda.model.Vianda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ViandaRepository extends JpaRepository<Vianda, Long>, JpaSpecificationExecutor<Vianda> {
}
