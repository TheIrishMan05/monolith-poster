package ifmo.poster.monolith.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Poster Monolith API",
                version = "1.0.0",
                description = "API монолитной системы покупки и продажи билетов на события",
                contact = @Contact(name = "ITMO HS Team")
        )
)
public class OpenApiConfig {
}
