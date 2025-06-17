package com.viandasApp.api.Utils;

import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

public class ErrorHandler {
    /**
     * Procesa los errores de validación y los convierte en un mapa de mensajes.
     *
     * @param result BindingResult que contiene los errores de validación.
     * @return Un mapa donde las claves son los nombres de los campos y los valores son los mensajes de error.
     */
    public static Map<String, String> procesarErrores(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(
                error -> errores.put(error.getField(), error.getDefaultMessage())
        );
        return errores;
    }
}
