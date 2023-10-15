package com.finman.finmanbackend;

import com.finman.finmanbackend.security.SecurityConfig;
import com.finman.finmanbackend.security.jwt.JwtUtil;
import com.finman.finmanbackend.user.User;
import com.finman.finmanbackend.user.UserRole;
import lombok.With;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockController.class)
@Import(value = {SecurityConfig.class})
class SecurityConfigTest {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    MockMvc mockMvc;


    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    @Qualifier("userRepositoryBasedUserDetailsServiceImpl")
    UserDetailsService userDetailsService;


    @BeforeEach
    void init() {
    }

    void testWithPathAndExpectedResultMatcher(String path, ResultMatcher resultMatcher) {
        try {
            mockMvc.perform(get(path))
                    .andExpect(resultMatcher);
        } catch (Exception e) {
            fail(e);
        }
    }
    @Test
    @WithAnonymousUser
    void testPermitAllWithAnAnonymousUser() {
        testWithPathAndExpectedResultMatcher("/permit-all", status().isOk());
    }

    @Test
    @WithMockUser(roles = "STANDARD")
    void testPermitAllWithAStandardUser() {
        testWithPathAndExpectedResultMatcher("/permit-all", status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPermitAllWithAnAdminUser() {
        testWithPathAndExpectedResultMatcher("/permit-all", status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testIsAdminWithAnAnonymousUser() {
        testWithPathAndExpectedResultMatcher("/is-admin", status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "STANDARD")
    void testIsAdminWithAStandardUser() {
        testWithPathAndExpectedResultMatcher("/is-admin", status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testIsAdminWithAdminUser() {
        testWithPathAndExpectedResultMatcher("/is-admin", status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testIsStandardWithAnAnonymousUser() {
        testWithPathAndExpectedResultMatcher("/is-user", status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "STANDARD")
    void testIsStandardWithAStandardUser() {
        testWithPathAndExpectedResultMatcher("/is-user", status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testIsStandardWithAnAdminUser() {
        testWithPathAndExpectedResultMatcher("/is-user", status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testIsAuthenticatedWithAnAnonymousUser() {
        testWithPathAndExpectedResultMatcher("/is-authenticated", status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "STANDARD")
    void testIsAuthenticatedWithAStandardUser() {
        testWithPathAndExpectedResultMatcher("/is-authenticated", status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testIsAuthenticatedWithAnAdminUser() {
        testWithPathAndExpectedResultMatcher("/is-authenticated", status().isOk());
    }


    @Test
    @WithAnonymousUser
    void testIsAnonymousWithAnAnonymousUser() {
        testWithPathAndExpectedResultMatcher("/is-anonymous", status().isOk());
    }

    @Test
    @WithMockUser(roles = "STANDARD")
    void testIsAnonymousWithAStandardUser() {
        testWithPathAndExpectedResultMatcher("/is-anonymous", status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testIsAnonymousWithAnAdminUser() {
        testWithPathAndExpectedResultMatcher("/is-anonymous", status().isForbidden());
    }
}

@RestController
class MockController {
    @PreAuthorize("permitAll()")
    @GetMapping("/permit-all")
    void permitAll() {

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/is-admin")
    void isAdmin() {

    }

    @PreAuthorize("hasRole('ROLE_STANDARD')")
    @GetMapping("/is-user")
    void isNonAdminUser() {

    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/is-anonymous")
    void isAnonymous() {

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/is-authenticated")
    void isAuthenticated() {

    }

}
