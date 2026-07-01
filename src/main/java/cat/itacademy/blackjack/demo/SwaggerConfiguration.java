package cat.itacademy.blackjack.demo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfiguration implements WebMvcConfigurer {
        @Bean
        public OpenAPI blackjackOpenAPI() {
            return new OpenAPI()
                    .info(new Info()
                            .title("Blackjack API")
                            .description("Interactive Blackjack game API documentation")
                            .version("v1.0"));
        }
    }