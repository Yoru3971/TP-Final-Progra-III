package com.viandasApp.api.Utils;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Clase que maneja globalmente las excepciones lanzadas por los controladores de la API.
 * Permite centralizar el manejo de errores y devolver respuestas claras, estructuradas y coherentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación (por ejemplo, fallos en @Valid).
     * Captura todos los errores de campos y los retorna en un mapa.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        // Recorre cada error de campo y agrega el campo y su mensaje al mapa
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(errores);
    }

    /**
     * Maneja excepciones específicas lanzadas con ResponseStatusException,
     * como las que usamos para devolver errores 404, 400, etc. desde los servicios.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getReason());

        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    /**
     * Maneja excepciones de constraints no respetadas. Muestra las variables que
     * no fueron respetadas y su respectivo mensaje.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();

        // Obtener el mensaje de las violaciones con el nombre de la variable
        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(violation -> {
                    String fieldName = violation.getPropertyPath().toString(); // Nombre del campo
                    String message = violation.getMessage(); // Mensaje de la violación
                    return fieldName + ": " + message; // Combinamos el campo con el mensaje
                })
                .collect(Collectors.toList());

        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // Error de validación, BAD_REQUEST
        body.put("errors", errorMessages); // Mostrar los mensajes de error con el nombre de la variable

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja cualquier otra excepción inesperada que no esté capturada arriba.
     * Esto evita que se muestren trazas internas del servidor al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Hubo un error con los datos ingresados o con el servidor.");

        // Solo para depuración (podés loguear ex.getMessage() si querés)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}