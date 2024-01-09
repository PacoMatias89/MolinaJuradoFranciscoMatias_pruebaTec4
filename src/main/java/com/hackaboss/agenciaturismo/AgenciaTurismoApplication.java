package com.hackaboss.agenciaturismo;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class AgenciaTurismoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgenciaTurismoApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API de agencia turística")
                .version("0.0.1")
                .description("Prueba técnica de agencia turística"));
    }

}
