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
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        Map<String, Object> envMap = new HashMap<>();

        // --- Base de datos ---
        if (dotenv.get("spring_datasource_url") != null)
            envMap.put("spring.datasource.url", dotenv.get("spring_datasource_url"));

        if (dotenv.get("spring_datasource_username") != null)
            envMap.put("spring.datasource.username", dotenv.get("spring_datasource_username"));

        if (dotenv.get("spring_datasource_password") != null)
            envMap.put("spring.datasource.password", dotenv.get("spring_datasource_password"));

        // --- JWT ---
        if (dotenv.get("jwt_secret") != null)
            envMap.put("jwt.secret", dotenv.get("jwt_secret"));

        if (dotenv.get("jwt_expiration_ms") != null)
            envMap.put("jwt.expiration_ms", dotenv.get("jwt_expiration_ms"));

        // --- Refresh Token ---
        if (dotenv.get("jwt.refresh-expiration-ms") != null)
            envMap.put("jwt.refresh-expiration-ms", dotenv.get("jwt.refresh-expiration-ms"));

        // --- Cookie ---
        if (dotenv.get("jwt.cookie.secure") != null)
            envMap.put("jwt.cookie.secure", dotenv.get("jwt.cookie.secure"));

        // --- Cloudinary ---
        if (dotenv.get("cloudinary_cloud_name") != null)
            envMap.put("cloudinary.cloud_name", dotenv.get("cloudinary_cloud_name"));

        if (dotenv.get("cloudinary_api_key") != null)
            envMap.put("cloudinary.api_key", dotenv.get("cloudinary_api_key"));

        if (dotenv.get("cloudinary_api_secret") != null)
            envMap.put("cloudinary.api_secret", dotenv.get("cloudinary_api_secret"));

        // --- Clarifai ---
        if (dotenv.get("clarifai.api_key") != null)
            envMap.put("clarifai.api_key", dotenv.get("clarifai.api_key"));

        // --- Mail ---
        if (dotenv.get("spring.mail.host") != null)
            envMap.put("spring.mail.host", dotenv.get("spring.mail.host"));

        if (dotenv.get("spring.mail.port") != null)
            envMap.put("spring.mail.port", dotenv.get("spring.mail.port"));

        if (dotenv.get("spring.mail.username") != null)
            envMap.put("spring.mail.username", dotenv.get("spring.mail.username"));

        if (dotenv.get("spring.mail.password") != null)
            envMap.put("spring.mail.password", dotenv.get("spring.mail.password"));

        if (dotenv.get("spring.mail.properties.mail.smtp.auth") != null)
            envMap.put("spring.mail.properties.mail.smtp.auth", dotenv.get("spring.mail.properties.mail.smtp.auth"));

        if (dotenv.get("spring.mail.properties.mail.smtp.starttls.enable") != null)
            envMap.put("spring.mail.properties.mail.smtp.starttls.enable", dotenv.get("spring.mail.properties.mail.smtp.starttls.enable"));

        // --- Login con Gmail ---
        if (dotenv.get("google.client.id") != null) {
            envMap.put("google.client.id", dotenv.get("google.client.id"));
        }

        propertySources.addFirst(new MapPropertySource("dotenvProperties", envMap));
        System.out.println("Loaded .env variables: " + envMap); // Para debug
    }
}


