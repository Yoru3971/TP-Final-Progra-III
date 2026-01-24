package com.viandasApp.api.ServiceGenerales.imageValidation.strategies;

import com.viandasApp.api.ServiceGenerales.imageValidation.TipoValidacion;
import org.springframework.stereotype.Component;

@Component
public class EmprendimientoValidationStrategy implements ValidationStrategy {

    @Override
    public boolean supports(TipoValidacion tipo) {
        return tipo == TipoValidacion.EMPRENDIMIENTO;
    }

    @Override
    public String getPrompt() {
        return """
            CONTEXTO:
            Imagen de portada o perfil para un negocio gastronómico (Restaurante, Catering o Cocinero amateur).

            OBJETIVO:
            Validar que la imagen represente una marca o un entorno de trabajo culinario profesional.

            CRITERIOS DE ACEPTACIÓN:
            - Logotipos, isologos o marcas gráficas del negocio.
            - Fachadas de locales, food trucks o puestos de venta.
            - Fotos del equipo de cocina trabajando o "Action shots" culinarios.
            - Bodegones de comida o collages de varios platos.
            - Menús o pizarras con ofertas gastronómicas.

            CRITERIOS DE RECHAZO:
            - Rechazar selfies personales fuera de contexto laboral (ej: en la playa, en el baño).
            - Rechazar imágenes genéricas que no aporten valor de marca (ej: foto borrosa del suelo, capturas de pantalla de celular).
            - Rechazar contenido sensible: violencia, drogas, armas o desnudez.

            FORMATO DE RESPUESTA OBLIGATORIO:
            Debes responder ÚNICAMENTE un objeto JSON válido (sin markdown) con esta estructura exacta:
            {
                "aprobado": boolean,
                "motivo": "Si es false, explica brevemente el motivo en español. Si es true, dejalo vacío."
            }
            """;
    }
}