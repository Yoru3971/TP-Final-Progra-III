package com.viandasApp.api;

import com.viandasApp.api.Config.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ViandasApiApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ViandasApiApplication.class);
		app.addInitializers(new DotenvInitializer()); // <-- carga variables .env antes de arrancar
		app.run(args);
	}
}


