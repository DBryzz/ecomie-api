package org.csbf.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                // Allow access to unauthenticated users
                .requestMatchers("/api/v1/auth/**", "/api/v1/demo-controller**").permitAll()
                // Allow swagger routes
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "**/swagger-resources/**", "/v2/api-docs", "/v3/api-docs/**", "/webjars/**").permitAll()
//                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // permit users with roles, USER
                .requestMatchers("/api/v1/secure/user**").hasAnyAuthority("USER")
                .requestMatchers("/api/v1/secure/ecomiest**").hasAnyAuthority("ECOMIEST")
                // Permit only ADMIN users
                .requestMatchers("/api/v1/secure/admin**").hasAuthority("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
