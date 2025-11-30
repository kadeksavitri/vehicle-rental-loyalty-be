// package apap.ti._5.vehicle_rental_2306203236_be.security;

// import apap.ti._5.vehicle_rental_2306203236_be.security.apikey.ApiKeyAuthFilter;
// import apap.ti._5.vehicle_rental_2306203236_be.config.security.JwtAuthenticationFilter;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Lazy;
// import org.springframework.core.annotation.Order;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.access.AccessDeniedHandler;
// import org.springframework.security.web.authentication.HttpStatusEntryPoint;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
 
// import java.io.IOException;
// import java.util.Arrays;
 
// @Configuration
// @EnableWebSecurity
// @EnableMethodSecurity(prePostEnabled = true)
// public class WebSecurityConfig {

//     @Value("${CORS_ALLOWED_ORIGINS}")
//     private String allowedOrigins;
 
//     @Autowired
//     private JwtAuthenticationFilter jwtTokenFilter;
 
//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();

//         String[] origins = allowedOrigins.split(",");

//         configuration.setAllowedOriginPatterns(Arrays.asList(origins));
//         configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//         configuration.setAllowedHeaders(Arrays.asList("*"));
//         configuration.setAllowCredentials(true);
//         configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);
//         return source;
//     }

 
//     @Bean
//     @Order(1)
//     public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
//         http
//             .securityMatcher("/api/**")
//             .cors(Customizer.withDefaults())   // WAJIB
//             .csrf(csrf -> csrf.disable())
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers(
//                     "/api/auth/register",
//                     "/api/auth/login",
//                     "/api/auth/validate-token"
//                 ).permitAll()
//                 .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
//                 .anyRequest().authenticated()
//             )
//             .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
//             .formLogin(form -> form.disable())
//             .httpBasic(basic -> basic.disable());

//         return http.build();
//     }

 
//     // ===================== WEB SECURITY (untuk /login, /css, dll) =====================
//     @Bean
//     @Order(2)
//     public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
//         http
//             .securityMatcher("/**")  // Match semua NON-API requests
//             .authorizeHttpRequests(requests -> requests
  
//                 .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
//                 .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
//                 .anyRequest().authenticated()
//             )
//             .formLogin(form -> form
//                 .loginPage("/login")
//                 .permitAll()
//                 .defaultSuccessUrl("/")
//             )
//             .logout(logout -> logout
//                 .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                 .logoutSuccessUrl("/login")
//             )
//             .exceptionHandling(handling -> handling
//                 .accessDeniedHandler((request, response, accessDeniedException) -> {
//                     response.sendRedirect("/access-denied");
//                 })
//             );
 
//         return http.build();
//     }
 
//     // ===================== AUTH MANAGER & PASSWORD ENCODER =====================
//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//         return authConfig.getAuthenticationManager();
//     }
 
//     @Bean
//     public BCryptPasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

// }