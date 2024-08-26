package com.example.member.Config;
//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.security.SecurityScheme;
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";

        // Define the security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        // Define the security scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Define the components
        Components components = new Components().addSecuritySchemes(jwt, securityScheme);

        // Create and return the OpenAPI configuration
        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
                .title("API Test") // API의 제목
                .description("Let's practice Swagger UI") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}