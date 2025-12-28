package com.viandasApp.api.Reclamo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reclamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigoTicket;

    @Column(nullable = false)
    private String emailUsuario;

    @Enumerated(EnumType.STRING)
    private CategoriaReclamo categoria;

    @Column(nullable = false, length = 400)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoReclamo estado;

    private LocalDateTime fechaCreacion;
}
