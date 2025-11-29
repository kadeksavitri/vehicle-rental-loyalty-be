package apap.ti._5.vehicle_rental_2306203236_be.security.apikey;

import apap.ti._5.vehicle_rental_2306203236_be.security.SecurityRoles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

	// @Value("${vehicle.api-key:test-api-key-dev}") test
	//@Value("${vehicle.api-key}")
	@Value("${API_KEY}")
	private String expectedApiKey;

	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
								    HttpServletResponse response,
								    FilterChain filterChain)
			throws ServletException, IOException {

		String apiKey = request.getHeader("X-API-KEY");

		if (apiKey != null && !apiKey.isEmpty() && apiKey.equals(expectedApiKey)) {
			UsernamePasswordAuthenticationToken auth =
					new UsernamePasswordAuthenticationToken(
							"api-key-client",
							null,
							Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + SecurityRoles.API_KEY))
					);

			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		System.out.println("[API KEY CHECK] expected=" + expectedApiKey);

		filterChain.doFilter(request, response);
	}
}