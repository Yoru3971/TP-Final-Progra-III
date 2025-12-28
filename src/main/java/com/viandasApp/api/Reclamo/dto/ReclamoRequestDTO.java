package com.viandasApp.api.Reclamo.dto;

import com.viandasApp.api.Reclamo.model.CategoriaReclamo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReclamoRequestDTO {
    @NotNull(message = "La categoría es obligatoria")
    private CategoriaReclamo categoria;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 400, message = "La descripción no puede superar los 400 caracteres")
    private String descripcion;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;
}
