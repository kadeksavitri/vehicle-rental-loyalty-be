package apap.ti._5.vehicle_rental_2306203236_be.security.jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;


public class JwtUserDetails implements UserDetails {
    private final UUID id;
    private final String username;
    private final String email;
    private final String name;
    private final String role;

    public JwtUserDetails(UUID id, String username, String email, String name, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return role without ROLE_ prefix so @PreAuthorize works correctly
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return null; // No password needed for JWT authentication
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}