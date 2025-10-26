package com.viandasApp.api.Config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Dotenv dotenv = Dotenv.configure()
                .directory("D:/Kevin/Universidad/Programacion/4to cuatrimestre/Programacion 4/API Mi Viandita/api/") // <-- CAmbiar
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        Map<String, Object> envMap = new HashMap<>();

        // Base de datos
        if (dotenv.get("spring_datasource_url") != null)
            envMap.put("spring.datasource.url", dotenv.get("spring_datasource_url"));
        if (dotenv.get("spring_datasource_username") != null)
            envMap.put("spring.datasource.username", dotenv.get("spring_datasource_username"));
        if (dotenv.get("spring_datasource_password") != null)
            envMap.put("spring.datasource.password", dotenv.get("spring_datasource_password"));

        // Cloudinary
        if (dotenv.get("CLOUDINARY_CLOUD_NAME") != null)   // en .env usás cloudinary.cloud_name
            envMap.put("cloudinary.cloud_name", dotenv.get("CLOUDINARY_CLOUD_NAME"));
        if (dotenv.get("CLOUDINARY_API_KEY") != null)
            envMap.put("cloudinary.api_key", dotenv.get("CLOUDINARY_API_KEY"));
        if (dotenv.get("CLOUDINARY_API_SECRET") != null)
            envMap.put("cloudinary.api_secret", dotenv.get("CLOUDINARY_API_SECRET"));

        // JWT
        if (dotenv.get("jwt_secret") != null)
            envMap.put("jwt.secret", dotenv.get("jwt_secret"));
        if (dotenv.get("jwt_expiration_ms") != null)
            envMap.put("jwt.expiration_ms", dotenv.get("jwt_expiration_ms"));

        propertySources.addFirst(new MapPropertySource("dotenvProperties", envMap));
        //System.out.println("Loaded .env variables: " + envMap); // Para debug
    }
}


