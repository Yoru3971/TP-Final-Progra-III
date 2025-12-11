package com.viandasApp.api.ServiceGenerales;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.*;

@Service
public class ImageValidationService {

    @Value("${clarifai.api_key}")
    private String clarifaiApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // ID de "General Image Recognition" público de Clarifai
    private static final String MODELO_GENERAL_ID = "aaa03c23b3724a16a56b629203edc62c";
    // ID de modelo de comida
    private static final String MODELO_COMIDA_ID = "bd367be194cf45149e75f01d59f77ba7";

    // LISTA DE PALABRAS CLAVE A BLOQUEAR
    private static final List<String> PALABRAS_PROHIBIDAS = Arrays.asList(
            "weapon", "gun", "pistol", "rifle", "firearm", "knife", "sword",
            "blood", "gore", "injury",
            "drugs", "cannabis", "cocaine", "pills", "syringe",
            "nude", "naked", "erotic", "porn"
    );

    private static final String CLARIFAI_URL = "https://api.clarifai.com/v2/models/";

    public ImageValidationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public enum TipoValidacion {
        VIANDA, PERFIL, NINGUNA
    }

    public void validarImagen(MultipartFile file, TipoValidacion tipoValidacion) {
        try {
            byte[] imagenBytes = file.getBytes();
            validarImagenInterno(imagenBytes, tipoValidacion);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al leer bytes de la imagen");
        }
    }

    private void validarImagenInterno(byte[] imagenBytes, TipoValidacion tipoValidacion) {
        if (tipoValidacion == TipoValidacion.NINGUNA) return;

        // Validar Moderación
        verificarModeracion(imagenBytes);

        // Validar Comida
        if (tipoValidacion == TipoValidacion.VIANDA) {
            if (!verificarSiEsComida(imagenBytes)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La IA no detectó comida en la imagen. Sube una foto clara.");
            }
        }
    }

    private void verificarModeracion(byte[] imageBytes) {
        JsonNode outputs = llamarClarifaiHttp(imageBytes, MODELO_GENERAL_ID);

        JsonNode concepts = outputs.get(0).path("data").path("concepts");

        System.out.println("----- ANÁLISIS DE OBJETOS DETECTADOS -----");

        for (JsonNode concepto : concepts) {
            String nombre = concepto.path("name").asText().toLowerCase(); // Convertimos a minúsculas
            double probabilidad = concepto.path("value").asDouble();

            if (probabilidad > 0.80) {
                System.out.println("Detectado: " + nombre + " (" + probabilidad + ")");
            }

            for (String prohibido : PALABRAS_PROHIBIDAS) {
                if (nombre.contains(prohibido) && probabilidad > 0.85) {
                    System.err.println("BLOQUEADO POR: " + nombre);
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Imagen rechazada. Se detectó contenido no permitido: " + traducirConcepto(nombre));
                }
            }
        }
        System.out.println("------------------------------------------");
    }

    private String traducirConcepto(String conceptoEnIngles) {
        if (conceptoEnIngles.contains("gun") || conceptoEnIngles.contains("weapon") || conceptoEnIngles.contains("rifle")) return "Armas";
        if (conceptoEnIngles.contains("blood") || conceptoEnIngles.contains("gore")) return "Violencia";
        if (conceptoEnIngles.contains("nude") || conceptoEnIngles.contains("naked")) return "Contenido explícito";
        if (conceptoEnIngles.contains("drug") || conceptoEnIngles.contains("pill")) return "Drogas";
        return "Contenido restringido (" + conceptoEnIngles + ")";
    }

    private boolean verificarSiEsComida(byte[] imageBytes) {
        try {
            JsonNode outputs = llamarClarifaiHttp(imageBytes, MODELO_COMIDA_ID);
            JsonNode concepts = outputs.get(0).path("data").path("concepts");

            for (JsonNode concepto : concepts) {
                if (concepto.path("value").asDouble() > 0.85) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Advertencia: Falló detección de comida: " + e.getMessage());
            return true;
        }
    }

    private JsonNode llamarClarifaiHttp(byte[] imageBytes, String modelId) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            Map<String, Object> body = new HashMap<>();

            // UserAppId para acceder a modelos públicos
            Map<String, String> userAppId = new HashMap<>();
            userAppId.put("user_id", "clarifai");
            userAppId.put("app_id", "main");
            body.put("user_app_id", userAppId);

            Map<String, Object> data = new HashMap<>();
            Map<String, String> image = new HashMap<>();
            image.put("base64", base64Image);
            data.put("image", image);

            Map<String, Object> input = new HashMap<>();
            input.put("data", data);

            body.put("inputs", Collections.singletonList(input));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Key " + clarifaiApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            String url = CLARIFAI_URL + modelId + "/outputs";
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());

            // Verificamos status de la respuesta de Clarifai
            if (root.path("status").path("code").asInt() != 10000) { // 10000 es SUCCESS en Clarifai
                throw new RuntimeException("Error de API: " + root.path("status").path("description").asText());
            }

            return root.path("outputs");

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de comunicación con IA: " + e.getMessage());
        }
    }
}
