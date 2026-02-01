package com.viandasApp.api.ServiceGenerales.imageValidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viandasApp.api.ServiceGenerales.imageValidation.strategies.ValidationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
public class ImageValidationService {

    @Value("${openai.api_key}")
    private String openaiApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final List<ValidationStrategy> strategies;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public ImageValidationService(List<ValidationStrategy> strategies) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.strategies = strategies;
    }

    public void validarImagen(MultipartFile file, TipoValidacion tipoValidacion) {
        if (tipoValidacion == TipoValidacion.NINGUNA) return;

        ValidationStrategy strategy = strategies.stream()
                .filter(s -> s.supports(tipoValidacion))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Estrategia no encontrada para: " + tipoValidacion));

        try {
            byte[] imagenBytes = file.getBytes();
            callOpenAI(imagenBytes, strategy.getPrompt());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al leer el archivo de imagen.");
        }
    }

    private void callOpenAI(byte[] imagenBytes, String promptEstrategia) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + openaiApiKey);

            String base64Image = Base64.getEncoder().encodeToString(imagenBytes);
            String dataUrl = "data:image/jpeg;base64," + base64Image;

            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o-mini");
            body.put("max_tokens", 150);

            Map<String, String> jsonFormat = new HashMap<>();
            jsonFormat.put("type", "json_object");
            body.put("response_format", jsonFormat);

            List<Map<String, Object>> messages = new ArrayList<>();

            // Mensaje del Sistema (Instrucciones / Prompt de la estrategia)
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", promptEstrategia + " Recuerda: Tu respuesta debe ser ESTRICTAMENTE un JSON.");
            messages.add(systemMessage);

            // Mensaje del Usuario (Imagen + Texto dummy)
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");

            List<Map<String, Object>> contentList = new ArrayList<>();

            // Texto
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", "Analiza esta imagen.");
            contentList.add(textContent);

            // Imagen
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            Map<String, Object> imageUrlMap = new HashMap<>();
            imageUrlMap.put("url", dataUrl);
            imageContent.put("image_url", imageUrlMap);
            contentList.add(imageContent);

            userMessage.put("content", contentList);
            messages.add(userMessage);

            body.put("messages", messages);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, request, String.class);

            interpretarRespuesta(response.getBody());

        } catch (ResponseStatusException e) {
            System.err.println("Error OpenAI: " + e.getMessage());
            throw e;

        } catch (Exception e) {
            System.err.println("Error crítico OpenAI: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servicio de validación de imágenes: " + e.getMessage());
        }
    }

    private void interpretarRespuesta(String jsonResponse) throws IOException {
        JsonNode root = objectMapper.readTree(jsonResponse);

        JsonNode choices = root.path("choices");
        if (choices.isEmpty()) {
            throw new RuntimeException("La IA no devolvió ninguna respuesta (choices vacío).");
        }

        String contentString = choices.get(0).path("message").path("content").asText();

        String jsonLimpio = contentString.replace("```json", "").replace("```", "").trim();

        JsonNode jsonIA = objectMapper.readTree(jsonLimpio);
        boolean aprobado = jsonIA.path("aprobado").asBoolean();
        String motivo = jsonIA.path("motivo").asText();

        if (!aprobado) {
            System.out.println("Rechazado por IA: " + motivo);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagen rechazada: " + motivo);
        }
        System.out.println("Aprobada por IA");
    }
}
