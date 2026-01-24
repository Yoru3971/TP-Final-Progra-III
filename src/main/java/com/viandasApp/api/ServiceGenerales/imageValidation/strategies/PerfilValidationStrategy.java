package com.viandasApp.api.ServiceGenerales.imageValidation.strategies;

import com.viandasApp.api.ServiceGenerales.imageValidation.TipoValidacion;
import org.springframework.stereotype.Component;

@Component
public class PerfilValidationStrategy implements ValidationStrategy {

    @Override
    public boolean supports(TipoValidacion tipo) {
        return tipo == TipoValidacion.PERFIL_USUARIO;
    }

    @Override
    public String getPrompt() {
        return """
            CONTEXTO:
            Foto de perfil para una red social de venta de comida. La confianza y la identidad son claves.

            OBJETIVO:
            Validar que la imagen permita identificar al usuario o representarlo mediante un avatar claro.

            CRITERIOS DE ACEPTACIÓN:
            - La imagen DEBE mostrar una persona real (rostro o cuerpo entero).
            - Se aceptan "selfies" y fotos grupales si el usuario es distinguible.
            - Se aceptan avatares, ilustraciones o dibujos estilo "cartoon" si representan una identidad.
            - La imagen debe tener suficiente iluminación para ser distinguible.

            CRITERIOS DE RECHAZO:
            - Rechazar objetos sueltos sin contexto humano (ej: una silla, un zapato).
            - Rechazar paisajes vacíos o fotos totalmente negras/oscuras.
            - Rechazar animales solos (a menos que estén con una persona).
            - Rechazar estrictamente: desnudez parcial o total, gestos obscenos, armas o violencia.

            FORMATO DE RESPUESTA OBLIGATORIO:
            Debes responder ÚNICAMENTE un objeto JSON válido (sin markdown) con esta estructura exacta:
            {
                "aprobado": boolean,
                "motivo": "Si es false, explica brevemente el motivo en español (ej: 'No se detecta una persona'). Si es true, dejalo vacío."
            }
            """;
    }
}
