package backend.mossy.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mossyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mossy API Documentation")
                        .description("Mossy 이커머스 플랫폼 API 문서")
                        .version("v1.0.0")
                )

                //JWT 인증 설정
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(
                        new Components().addSecuritySchemes(
                                "BearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
