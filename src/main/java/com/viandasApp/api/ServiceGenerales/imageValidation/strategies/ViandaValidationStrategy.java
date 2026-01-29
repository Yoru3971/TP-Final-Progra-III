package com.viandasApp.api.ServiceGenerales.imageValidation.strategies;

import com.viandasApp.api.ServiceGenerales.imageValidation.TipoValidacion;
import org.springframework.stereotype.Component;

@Component
public class ViandaValidationStrategy implements ValidationStrategy {

    @Override
    public boolean supports(TipoValidacion tipo) {
        return tipo == TipoValidacion.VIANDA;
    }

    @Override
    public String getPrompt() {
        return """
        CONTEXTO:
        Esta aplicación conecta emprendedores gastronómicos con clientes mediante viandas de comida.

        OBJETIVO:
        Validar que la imagen subida represente un plato o preparación de comida real, apta para ser publicada.

        CRITERIOS DE ACEPTACIÓN:
        - La imagen debe mostrar comida o un plato preparado.
        - Puede incluir utensilios de cocina normales (cuchillos, tenedores, tablas, sartenes).
        - Se aceptan manos del emprendedor manipulando la comida o cocinando.
        - La comida debe ser el foco principal de la imagen.

        CRITERIOS DE RECHAZO:
        - Rechazar si el foco principal son rostros, animales o paisajes sin comida.
        - Rechazar si la imagen es oscura, borrosa o no contiene comida real.
        - Rechazar estrictamente violencia, sangre, armas o suciedad evidente.

        FORMATO DE RESPUESTA OBLIGATORIO:
        Debes responder ÚNICAMENTE un objeto JSON válido (sin markdown, sin comillas extra) con esta estructura exacta:
        {
            "aprobado": boolean,
            "motivo": "Si es false, explica brevemente por qué en español. Si es true, dejalo vacío."
        }
        """;
    }
}
