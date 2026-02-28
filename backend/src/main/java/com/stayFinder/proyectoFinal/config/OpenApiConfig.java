package com.stayFinder.proyectoFinal.config;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "StayFinder API",
                version = "v1",
                description = "API para gestión de alojamientos, reservas y comentarios",
                contact = @Contact(name = "Equipo StayFinder", email = "soporte@stayfinder.com")
        ),
        servers = {
                @Server(url = "/", description = "Servidor local")
        }
)
@Configuration
public class OpenApiConfig {
    // Config global de OpenAPI si más adelante la necesitas
}
