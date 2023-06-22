package com.finman.finmanbackend.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Responsible for intercepting the authentication header
 * and authenticating the user based on a JWT token if it exists.
 *
 * @see OncePerRequestFilter
 * @see jakarta.servlet.Filter
 * @see UserDetailsService
 * @author AdamBalski
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    UserDetailsService userDetailsService;
    JwtUtil jwtUtil;

    @Autowired
    public JwtFilter(@Qualifier("userRepositoryBasedUserDetailsServiceImpl") UserDetailsService userDetailsService,
                     JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Look for the authentication header, remove 'Bearer ' part, process, authenticate the user.
     * If the token is non-existent, empty or not a 'Bearer ' prefixed token do nothing
     * If the token is existent, but invalid (expired, malformed, etc.) the context is cleared.
     *
     * @see JwtUtil
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Optional<String> jwtToken = getJwtToken(request);
        if(jwtToken.isPresent()) {
            SecurityContextHolder.clearContext();
            try {
                String username = jwtUtil.verifyAndExtractUsername(jwtToken.get());
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                SecurityContextHolder.getContext().setAuthentication(userDetailsToAuthentication(userDetails));
            }
            catch(Exception e) {
                // pass, the context is cleared
                // if the request needs the client to be authenticated
                // it will throw 401 eventually
            }
        }
        filterChain.doFilter(request, response);
    }

    private Authentication userDetailsToAuthentication(@NonNull UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    private Optional<String> getJwtToken(@NonNull HttpServletRequest request) {
        String authHeader = request.getHeader("authorization");

        // header not included
        if(authHeader == null) {
            return Optional.empty();
        }
        // not a token
        if(!authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        // remove the 'Bearer ' part
        String token = authHeader.substring("Bearer ".length());
        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }
}
