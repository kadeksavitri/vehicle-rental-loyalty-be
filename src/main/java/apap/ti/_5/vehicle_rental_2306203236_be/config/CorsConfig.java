package apap.ti._5.vehicle_rental_2306203236_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class CorsConfig {

    // @Value("${CORS_ALLOWED_ORIGINS}")
    // private String allowedOrigins;


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // String[] origins = allowedOrigins.split(",");

                registry
                    .addMapping("/**")
                    // For development allow all origins (use allowedOriginPatterns to permit credentials)
                    //.allowedOriginPatterns("*")
                    .allowedOrigins("http://localhost:5173", "http://localhost:8080", "http://2306203236-be.hafizmuh.site",
                                "https://2306203236-fe.hafizmuh.site")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .exposedHeaders("Authorization", "Set-Cookie")
                    .maxAge(3600);
            }
        };
    }
}

