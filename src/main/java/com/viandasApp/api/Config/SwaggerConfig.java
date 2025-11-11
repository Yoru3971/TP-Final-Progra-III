package com.viandasApp.api.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MiViandita API")
                        .version("1.1")
                        .description("MiViandita – Pedí tu vianda online es una plataforma diseñada para facilitar la gestión de pedidos de viandas en emprendimientos alimenticios.\n" +
                                "A través de esta API, los usuarios pueden registrarse como clientes o dueños de emprendimientos, cargar viandas, gestionar pedidos y recibir notificaciones.\n" +
                                "El sistema digitaliza el proceso de compra de viandas, brindando una experiencia cómoda, rápida y segura."))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))
                .tags(List.of(
                        new Tag().name("Autenticación - Público").description("Controlador para la gestión de autenticación desde el rol de público"),
                        new Tag().name("Usuarios - Cliente/Dueño").description("Controlador para la gestión de usuarios desde el rol de cliente o dueño"),
                        new Tag().name("Emprendimientos - Público").description("Controlador para la gestión de emprendimientos desde el rol de público"),
                        new Tag().name("Viandas - Público").description("Controlador para la gestión de viandas desde el rol de público"),
                        new Tag().name("Emprendimientos - Cliente").description("Controlador para la gestión de emprendimientos desde el rol de cliente"),
                        new Tag().name("Viandas - Cliente").description("Controlador para la gestión de viandas desde el rol de cliente"),
                        new Tag().name("Pedidos - Cliente").description("Controlador para la gestión de pedidos desde el rol de cliente"),
                        new Tag().name("Notificaciones - Cliente").description("Controlador para la gestión de notificaciones desde el rol de cliente"),
                        new Tag().name("Emprendimientos - Dueño").description("Controlador para la gestión de emprendimientos desde el rol de dueño"),
                        new Tag().name("Viandas - Dueño").description("Controlador para la gestión de viandas desde el rol de dueño"),
                        new Tag().name("Pedidos - Dueño").description("Controlador para la gestión de pedidos desde el rol de dueño"),
                        new Tag().name("Notificaciones - Dueño").description("Controlador para la gestión de notificaciones desde el rol de dueño"),
                        new Tag().name("Pedidos - Admin").description("Controlador para la gestión de pedidos desde el rol de administrador"),
                        new Tag().name("Notificaciones - Admin").description("Controlador para la gestión de notificaciones desde el rol de administrador"),
                        new Tag().name("Usuarios - Admin").description("Controlador para la gestión de usuarios desde el rol de administrador")
                ));
    }
}
