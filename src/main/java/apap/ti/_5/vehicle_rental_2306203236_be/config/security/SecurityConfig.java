package apap.ti._5.vehicle_rental_2306203236_be.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final ProfileClient profileClient;
    private final apap.ti._5.vehicle_rental_2306203236_be.security.apikey.ApiKeyAuthFilter apiKeyAuthFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecurityConfig(ProfileClient profileClient, apap.ti._5.vehicle_rental_2306203236_be.security.apikey.ApiKeyAuthFilter apiKeyAuthFilter) {
        this.profileClient = profileClient;
        this.apiKeyAuthFilter = apiKeyAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(profileClient);

        http
            .cors(cors -> cors.configure(http))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                // Diagnostic change: allow all requests so we can determine if 403 comes from ingress or app
                //.anyRequest().permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                        "Authentication diperlukan: " + authException.getMessage(),
                        "Silakan login terlebih dahulu atau periksa token Anda");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                        "Akses ditolak: Anda tidak memiliki permission untuk mengakses resource ini",
                        "Role Anda tidak sesuai dengan requirement endpoint ini");
                })
            )
            .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message, String data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status);
        errorResponse.put("message", message);
        errorResponse.put("data", data);
        errorResponse.put("timestamp", new Date());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
