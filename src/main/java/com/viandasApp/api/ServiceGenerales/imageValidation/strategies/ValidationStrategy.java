package com.viandasApp.api.ServiceGenerales.imageValidation.strategies;

import com.viandasApp.api.ServiceGenerales.imageValidation.TipoValidacion;

public interface ValidationStrategy {
    //Metodo para saber que si la estrategia valida ese tipo de imagen
    boolean supports(TipoValidacion tipo);
    String getPrompt();
}
