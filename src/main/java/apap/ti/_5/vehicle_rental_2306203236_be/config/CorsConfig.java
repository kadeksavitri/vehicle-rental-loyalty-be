package apap.ti._5.vehicle_rental_2306203236_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //implements WebMvcConfigurer
public class CorsConfig {

    @Value("${cors.allowed.origins:http://localhost:8080,http://localhost:5173,http://127.0.0.1:5173,http://2306219575-fe.hafizmuh.site,http://2306219575-be.hafizmuh.site,http://2306203236-be.hafizmuh.site,http://2306203236-fe.hafizmuh.site}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] origins = allowedOrigins.split(",");

                registry
                    .addMapping("/**")
                    .allowedOrigins(origins)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .exposedHeaders("Authorization", "Set-Cookie")
                    .maxAge(3600);
            }
        };
    }
}

