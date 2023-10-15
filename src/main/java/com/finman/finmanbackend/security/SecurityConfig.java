package com.finman.finmanbackend.security;

import com.finman.finmanbackend.security.jwt.JwtFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        AuthenticationEntryPoint authenticationEntryPoint = (request, response, authException) -> {
            // if user is not logged in
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "USER_IS_UNAUTHENTICATED");
                return;
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "USER_IS_UNAUTHORIZED");
        };

        // todo: stop ignoring csrf
        return http
            .securityMatcher("/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .addFilterBefore(jwtFilter, AnonymousAuthenticationFilter.class)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/**"))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                    .authenticationEntryPoint(authenticationEntryPoint)
            )
            .build();
    }
}
