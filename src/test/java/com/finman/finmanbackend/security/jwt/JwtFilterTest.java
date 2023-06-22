package com.finman.finmanbackend.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
    JwtFilter jwtFilter;
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    private MockHttpServletRequest req;
    private HttpServletResponse res;
    private FilterChain emptyFilterChain;
    private SecurityContext populatedContext;
    private UserDetails jwtUserDetails;
    private final List<GrantedAuthority> authorities = List.of();

    private boolean filterChainWasRun;

    @BeforeEach
    public void init() {
        jwtFilter = new JwtFilter(userDetailsService, jwtUtil);

        // immutable
        Authentication priorAuthenticatedUser = new TestingAuthenticationToken(null, null);
        populatedContext = new SecurityContextImpl(priorAuthenticatedUser);

        jwtUserDetails = new UserDetails() {

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return authorities;
            }

            @Override
            public String getPassword() {
                return "jwt-password";
            }

            @Override
            public String getUsername() {
                return "jwt-user";
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };

        SecurityContextHolder.clearContext();
        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();

        filterChainWasRun = false;
        emptyFilterChain = (request, response) -> filterChainWasRun = true;
    }

    @AfterEach
    public void assertFilterChainWasInvoked() {
        assertTrue(filterChainWasRun);
    }

    @Test
    void testTheFilterWithNoAuthHeaderAndClearedContext() throws Exception {
        jwtFilter.doFilterInternal(req, res, emptyFilterChain);
        assertEmptyContext();
    }

    @Test
    void testTheFilterWithNoAuthHeaderAndPopulatedContext() throws Exception {
        SecurityContextHolder.setContext(populatedContext);
        jwtFilter.doFilterInternal(req, res, emptyFilterChain);
        assertContextDidntChange();
    }

    @Test
    void testTheFilterWithNotEmptyNonBearerAuthHeaderAndPopulatedContext() throws Exception {
        SecurityContextHolder.setContext(populatedContext);
        req.addHeader("authorization", "nonsense");
        jwtFilter.doFilterInternal(req, res, emptyFilterChain);
        assertContextDidntChange();
    }

    @Test
    void testTheFilterWithNotEmptyNonBearerAuthHeaderAndClearedContext() throws Exception {
        req.addHeader("authorization", "nonsense");
        jwtFilter.doFilterInternal(req, res, emptyFilterChain);
        assertEmptyContext();
    }

    @Test
    void testTheFilterWithEmptyAuthHeaderAndPopulatedContext() throws Exception {
        SecurityContextHolder.setContext(populatedContext);
        req.addHeader("authorization", "");
        jwtFilter.doFilterInternal(req, res, emptyFilterChain);
        assertContextDidntChange();
    }

    @Test
    void testTheFilterWithEmptyAuthHeaderAndClearedContext() throws Exception {
        req.addHeader("authorization", "");
        jwtFilter.doFilterInternal(req, res, emptyFilterChain);
        assertEmptyContext();
    }

    @Test
    void testTheFilterWithEmptyBearerAuthHeaderAndPopulatedContext() throws Exception {
        SecurityContextHolder.setContext(populatedContext);
        req.addHeader("authorization", "Bearer ");
        jwtFilter.doFilterInternal(req, res, emptyFilterChain);
        assertContextDidntChange();
    }

    @Test
    void testTheFilterWithEmptyBearerAuthHeaderAndClearedContext() throws Exception {
        req.addHeader("authorization", "Bearer ");
        jwtFilter.doFilterInternal(req, res, emptyFilterChain);
        assertEmptyContext();
    }

    @Test
    void testTheFilterWithInvalidTokenAndPopulatedContext() throws Exception {
        req.addHeader("authorization", "Bearer gibberish");
        SecurityContextHolder.setContext(populatedContext);
        when(jwtUtil.verifyAndExtractUsername("gibberish")).thenThrow(JwtException.class);

        jwtFilter.doFilterInternal(req, res, emptyFilterChain);

        verify(jwtUtil, times(1)).verifyAndExtractUsername("gibberish");
        assertEmptyContext();
    }

    @Test
    void testTheFilterWithInvalidTokenAuthHeaderAndClearedContext() throws Exception {
        req.addHeader("authorization", "Bearer gibberish");
        when(jwtUtil.verifyAndExtractUsername("gibberish")).thenThrow(JwtException.class);

        jwtFilter.doFilterInternal(req, res, emptyFilterChain);

        verify(jwtUtil, times(1)).verifyAndExtractUsername("gibberish");
        assertEmptyContext();
    }

    @Test
    void testTheFilterWithValidTokenAndPopulatedContext() throws Exception {
        req.addHeader("authorization", "Bearer valid");
        SecurityContextHolder.setContext(populatedContext);
        when(jwtUtil.verifyAndExtractUsername("valid")).thenReturn("jwt-user");
        when(userDetailsService.loadUserByUsername("jwt-user")).thenReturn(jwtUserDetails);

        jwtFilter.doFilterInternal(req, res, emptyFilterChain);

        verify(jwtUtil, times(1)).verifyAndExtractUsername("valid");
        verify(userDetailsService, times(1)).loadUserByUsername("jwt-user");
        assertNewJwtUserContext();
    }

    @Test
    void testTheFilterWithValidTokenAuthHeaderAndClearedContext() throws Exception {
        req.addHeader("authorization", "Bearer valid");
        SecurityContextHolder.clearContext();
        when(jwtUtil.verifyAndExtractUsername("valid")).thenReturn("jwt-user");
        when(userDetailsService.loadUserByUsername("jwt-user")).thenReturn(jwtUserDetails);

        jwtFilter.doFilterInternal(req, res, emptyFilterChain);

        verify(jwtUtil, times(1)).verifyAndExtractUsername("valid");
        verify(userDetailsService, times(1)).loadUserByUsername("jwt-user");
        assertNewJwtUserContext();
    }

    void assertNewJwtUserContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertEquals("jwt-user", authentication.getPrincipal());
        assertEquals("jwt-password", authentication.getCredentials());
        assertEquals(authorities, authentication.getAuthorities());
    }

    void assertEmptyContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }

    void assertContextDidntChange() {
        // populatedContext is immutable, so if they are ==, then they also are .equals()
        assertSame(populatedContext, SecurityContextHolder.getContext());
    }


}