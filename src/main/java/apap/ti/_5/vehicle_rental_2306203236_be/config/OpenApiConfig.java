// package apap.ti._5.vehicle_rental_2306203236_be.config;

// import io.swagger.v3.oas.models.Components;
// import io.swagger.v3.oas.models.OpenAPI;
// import io.swagger.v3.oas.models.info.Contact;
// import io.swagger.v3.oas.models.info.Info;
// import io.swagger.v3.oas.models.info.License;
// import io.swagger.v3.oas.models.security.SecurityRequirement;
// import io.swagger.v3.oas.models.security.SecurityScheme;
// import io.swagger.v3.oas.models.servers.Server;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import java.util.List;

// @Configuration
// public class OpenApiConfig {

//     @Bean
//     public OpenAPI customOpenAPI() {
//         return new OpenAPI()
//                 .info(new Info()
//                         .title("Tour Package API Documentation")
//                         .version("1.0.0")
//                         .description("REST API documentation for Tour Package Management System - Backend Service 2")
//                         .contact(new Contact()
//                                 .name("APAP TI 2025")
//                                 .email("apap@cs.ui.ac.id")
//                                 .url("https://cs.ui.ac.id"))
//                         .license(new License()
//                                 .name("MIT License")
//                                 .url("https://opensource.org/licenses/MIT")))
//                 .servers(List.of(
//                         new Server()
//                                 .url("https://2306219575-be.hafizmuh.site")
//                                 .description("Production Server"),
//                         new Server()
//                                 .url("http://localhost:8081")
//                                 .description("Local Development Server")))
//                 .addSecurityItem(new SecurityRequirement()
//                         .addList("JWT Cookie Authentication")
//                         .addList("Bearer Authentication"))
//                 .components(new Components()
//                         .addSecuritySchemes("JWT Cookie Authentication", new SecurityScheme()
//                                 .type(SecurityScheme.Type.APIKEY)
//                                 .in(SecurityScheme.In.COOKIE)
//                                 .name("JWT_TOKEN")
//                                 .description("JWT token stored in HTTP-only cookie"))
//                         .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
//                                 .type(SecurityScheme.Type.HTTP)
//                                 .scheme("bearer")
//                                 .bearerFormat("JWT")
//                                 .description("JWT token in Authorization header (Bearer <token>)")));
//     }
// }