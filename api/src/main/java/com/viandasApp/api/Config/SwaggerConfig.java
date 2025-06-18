package com.viandasApp.api.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("MiViandita API")
                        .version("1.0")
                        .description("MiViandita – Pedí tu vianda online es una plataforma diseñada para facilitar la gestión de pedidos de viandas en emprendimientos alimenticios. \n" +
                                "    A través de esta API, los usuarios pueden registrarse como clientes o dueños de emprendimientos, cargar viandas, gestionar pedidos y recibir notificaciones en tiempo real.\n" +
                                "    El sistema digitaliza el proceso de compra de viandas, brindando una experiencia cómoda, rápida y segura tanto para quienes venden como para quienes compran. \n" +
                                "    Está orientado a pequeños y medianos emprendimientos gastronómicos que deseen mejorar su alcance y eficiencia operativa."))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new Components().addSecuritySchemes("basicAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                ));
    }
}
