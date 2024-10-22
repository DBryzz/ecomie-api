package org.csbf.security.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.constant.Role;
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
                // permit all authenticated users
                .requestMatchers("/api/v1/secure/user/**").hasAnyAuthority(Role.USER.name(), Role.ECOMIEST.name(), Role.COACH.name(), Role.SPONSOR.name(), Role.PRAYER_WARRIOR.name(), Role.ADMIN.name(), Role.SUPER_ADMIN.name(), Role.MISSIONARY.name())
                // permit users with roles, ECOMIEST, ADMIN, SUPER_ADMIN
                .requestMatchers("/api/v1/secure/ecomiest/**").hasAnyAuthority(Role.ECOMIEST.name(), Role.ADMIN.name(), Role.SUPER_ADMIN.name())
                // Permit only ADMIN, SUPER_ADMIN
                .requestMatchers("/api/v1/secure/admin/**").hasAnyAuthority(Role.ADMIN.name(), Role.SUPER_ADMIN.name())
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
