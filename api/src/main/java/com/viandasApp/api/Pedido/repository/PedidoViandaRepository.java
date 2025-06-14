package com.viandasApp.api.Pedido.repository;

import com.viandasApp.api.Pedido.model.PedidoVianda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoViandaRepository extends JpaRepository<PedidoVianda, Long> {

}
