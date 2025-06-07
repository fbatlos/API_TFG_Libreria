package com.es.aplicacion.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {


    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("API LeafRead")
                    .version("2.4")
                    .description("Documentación de la API para gestión de libros, valoraciones, compras, tickets...")
                    .contact(
                        Contact()
                            .name("Francisco José Batista de los Santos")
                            .email("franbati1705@gmail.com")
                    )
            )
    }
}
