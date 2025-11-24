package apap.ti._5.vehicle_rental_2306203236_be.security.jwt;
 
import java.io.IOException;
import java.util.UUID;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
 
 
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
@Component
public class JwtTokenFilter extends OncePerRequestFilter{
    @Autowired
    private JwtUtils jwtUtils;
 
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
    private static final String JWT_COOKIE_NAME = "JWT_TOKEN";
 
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        try{
            String jwt = parseJwt(request);
            if(jwt != null && jwtUtils.validateJwtToken(jwt)){
                // Extract user info from JWT token directly (no database query needed)
                String id = jwtUtils.getIdFromJwtToken(jwt);
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                String email = jwtUtils.getEmailFromJwtToken(jwt);
                String name = jwtUtils.getNameFromJwtToken(jwt);
                String role = jwtUtils.getRoleFromJwtToken(jwt);

                // Create UserDetails from JWT claims
                JwtUserDetails userDetails = new JwtUserDetails(
                    UUID.fromString(id),
                    username,
                    email,
                    name,
                    role
                );

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
 
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }catch(Exception e){
            logger.error("Cannot set user authentication: {}", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
 
    /**
     * Parse JWT token from Cookie (primary) or Authorization header (fallback)
     * Priority: Cookie > Authorization Header
     */
    private String parseJwt(HttpServletRequest request){
        // Try to get token from cookie first
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (StringUtils.hasText(token)) {
                        logger.debug("🍪 JWT token found in cookie");
                        return token;
                    }
                }
            }
        }
        
        // Fallback: get token from Authorization header (for backward compatibility & microservices)
        String headerAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
            logger.debug("📝 JWT token found in Authorization header");
            return headerAuth.substring(7);
        }
        
        return null;
    }
}