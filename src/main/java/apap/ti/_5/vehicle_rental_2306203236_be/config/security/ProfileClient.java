package apap.ti._5.vehicle_rental_2306203236_be.config.security;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
public class ProfileClient {

        private final WebClient webClient;

        public ProfileClient(
            @Value("${profile.service.base-url:http://2306219575-be.hafizmuh.site}") String baseUrl,
            @Value("${profile.service.skip-ssl-validation:false}") boolean skipSslValidation
        ) {
        HttpClient httpClient = HttpClient.create();
        if (skipSslValidation) {
            // WARNING: insecure trust manager — only for development
            try {
                SslContext sslContext = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build();
                httpClient = HttpClient.create().secure(spec -> spec.sslContext(sslContext));
            } catch (Exception ex) {
                System.err.println("Failed to build insecure SSL context: " + ex.getMessage());
            }
        }

        this.webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
        }

    /**
     * Validate token by calling external profile microservice /api/auth/validate-token.
     * If token is null or empty, returns null.
     */
    public ProfileValidateResponse validateToken(String token) {
        if (token == null || token.isBlank()) return null;

        try {
            // The profile service accepts cookie-based or Authorization header. We'll call with header.
            Mono<ProfileValidateResponse> mono = webClient.post()
                    .uri("/api/auth/validate-token")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(ProfileValidateWrapper.class)
                    .map(ProfileValidateWrapper::getData)
                    .onErrorReturn(null);

            return mono.block();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Login via profile microservice and return full login data (including token).
     */
    public LoginResponse login(LoginRequest req) {
        try {
            // Use exchangeToMono to capture non-2xx responses and log response body for debugging
            Mono<LoginWrapper> mono = webClient.post()
                    .uri("/api/auth/login")
                    .bodyValue(req)
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return clientResponse.bodyToMono(LoginWrapper.class);
                        } else {
                            return clientResponse.bodyToMono(String.class)
                                    .map(body -> {
                                        System.err.println("Profile Service login non-2xx status=" + clientResponse.statusCode() + " body=" + body);
                                        return null;
                                    });
                        }
                    })
                    .onErrorResume(ex -> {
                        System.err.println("Profile Service login error: " + ex.getMessage());
                        ex.printStackTrace();
                        return Mono.empty();
                    });

            LoginWrapper wrapper = mono.block();
            if (wrapper == null) {
                System.err.println("Profile Service login returned null wrapper or non-2xx response — attempting fallback with 'email' field");
                try {
                    java.util.Map<String, String> fallback = new java.util.HashMap<>();
                    fallback.put("email", req.getUsername());
                    fallback.put("password", req.getPassword());

                    Mono<LoginWrapper> mono2 = webClient.post()
                            .uri("/api/auth/login")
                            .bodyValue(fallback)
                            .exchangeToMono(clientResponse -> {
                                if (clientResponse.statusCode().is2xxSuccessful()) {
                                    return clientResponse.bodyToMono(LoginWrapper.class);
                                } else {
                                    return clientResponse.bodyToMono(String.class)
                                            .map(body -> {
                                                System.err.println("Profile Service fallback login non-2xx status=" + clientResponse.statusCode() + " body=" + body);
                                                return null;
                                            });
                                }
                            })
                            .onErrorResume(ex -> {
                                System.err.println("Profile Service fallback login error: " + ex.getMessage());
                                ex.printStackTrace();
                                return Mono.empty();
                            });

                    wrapper = mono2.block();
                    if (wrapper == null) {
                        System.err.println("Profile Service fallback login also returned null");
                        return null;
                    }
                } catch (Exception ex) {
                    System.err.println("Profile Service fallback login exception: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                }
            }
            System.out.println("Profile Service login response status wrapper present");
            return wrapper.getData();
        } catch (Exception ex) {
            System.err.println("Profile Service login exception: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginWrapper {
        private Integer status;
        private String message;
        private LoginResponse data;

        public LoginResponse getData() { return data; }
        public void setData(LoginResponse data) { this.data = data; }
    }

    public static class LoginResponse {
        private String token;
        private String type;
        private String id;
        private String username;
        private String email;
        private String name;
        private String role;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class ProfileValidateWrapper {
        private Integer status;
        private String message;
        private ProfileValidateResponse data;

        public ProfileValidateResponse getData() { return data; }
        public void setData(ProfileValidateResponse data) { this.data = data; }
    }

    public static class ProfileValidateResponse {
        private Boolean valid;
        private String userId;
        private String username;
        private String email;
        private String name;
        private String role;

        public Boolean getValid() { return valid; }
        public void setValid(Boolean valid) { this.valid = valid; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    /**
     * Proxy register call. Accepts any payload and returns the response body as Object.
     */
    public Object register(Object payload) {
        try {
            Mono<Object> mono = webClient.post()
                    .uri("/api/auth/register")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .onErrorReturn(null);

            return mono.block();
        } catch (Exception ex) {
            return null;
        }
    }

    // Fetch user by id: GET /api/users/{id}
    public ProfileUserWrapper getUserById(String id) {
        try {
            Mono<ProfileUserWrapper> mono = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/users/{id}").build(id))
                    .retrieve()
                    .bodyToMono(ProfileUserWrapper.class)
                    .onErrorReturn(null);

            return mono.block();
        } catch (Exception ex) {
            return null;
        }
    }

    public static class ProfileUserWrapper {
        private Integer status;
        private String message;
        private ProfileUser data;

        public ProfileUser getData() { return data; }
        public void setData(ProfileUser data) { this.data = data; }
    }

    public static class ProfileUsersWrapper {
        private Integer status;
        private String message;
        private java.util.List<ProfileUser> data;

        public java.util.List<ProfileUser> getData() { return data; }
        public void setData(java.util.List<ProfileUser> data) { this.data = data; }
    }

    // Fetch all customers: GET /api/users/customers
    public ProfileUsersWrapper getCustomers() {
        try {
            Mono<ProfileUsersWrapper> mono = webClient.get()
                    .uri("/api/users/customers")
                    .retrieve()
                    .bodyToMono(ProfileUsersWrapper.class)
                    .onErrorReturn(null);

            return mono.block();
        } catch (Exception ex) {
            return null;
        }
    }

    // Fetch all users: GET /api/users
    public ProfileUsersWrapper getAllUsers() {
        try {
            Mono<ProfileUsersWrapper> mono = webClient.get()
                    .uri("/api/users")
                    .retrieve()
                    .bodyToMono(ProfileUsersWrapper.class)
                    .onErrorReturn(null);

            return mono.block();
        } catch (Exception ex) {
            return null;
        }
    }

    // Update user profile: PUT /api/users/{id}
    public ProfileUserWrapper updateUser(String id, Object payload) {
        try {
            Mono<ProfileUserWrapper> mono = webClient.put()
                    .uri(uriBuilder -> uriBuilder.path("/api/users/{id}").build(id))
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(ProfileUserWrapper.class)
                    .onErrorReturn(null);

            return mono.block();
        } catch (Exception ex) {
            return null;
        }
    }

    public static class ProfileUser {
        private String id;
        private String username;
        private String email;
        private String name;
        private String role;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
