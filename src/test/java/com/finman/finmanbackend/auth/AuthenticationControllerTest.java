package com.finman.finmanbackend.auth;

import com.finman.finmanbackend.security.SecurityConfig;
import com.finman.finmanbackend.security.jwt.JwtFilter;
import com.finman.finmanbackend.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = AuthenticationController.class)
@Import(value = {SecurityConfig.class})
public class AuthenticationControllerTest {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    MockMvc mockMvc;

    // ApplicationContext wants repositories (services -> repositories),
    // to inject to services,
    // but springboot can't instantiate repositories with @WebMvcTest, so we
    // have to create mocked beans.
    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    @Qualifier("userRepositoryBasedUserDetailsServiceImpl")
    UserDetailsService userDetailsService;

    @Autowired
    JwtFilter jwtFilter;

    @Test
    void testGetRefreshTokenIfUserDoesNotExist() throws Exception {
        // given
        Mockito.when(authenticationService.authorizeAndCreateRefreshTokenCookie(any(LoginDto.class)))
                .thenThrow(new HttpAuthExceptions.NoSuchUsername401());

        String json = """
                {
                    "identifier": "doesnt exist",
                    "password": "lol"
                }
                """;
        mockMvc.perform(
                post("/api/v1/auth/get-refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.cookie().doesNotExist("REFRESH_TOKEN"))
            .andExpect(MockMvcResultMatchers.status().reason("NO_SUCH_USERNAME"));
    }

    @Test
    void testGetRefreshTokenIfPasswordDoesNotMatch() throws Exception {
        Mockito.when(authenticationService.authorizeAndCreateRefreshTokenCookie(any(LoginDto.class)))
                .thenThrow(new HttpAuthExceptions.WrongPassword401());

        String json = """
                {
                    "identifier": "exists",
                    "password": "does not match"
                }
                """;
        mockMvc.perform(
                        post("/api/v1/auth/get-refresh-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.cookie().doesNotExist("REFRESH_TOKEN"))
                .andExpect(MockMvcResultMatchers.status().reason("WRONG_PASSWORD"));
    }

    @Test
    void testGetRefreshToken() throws Exception {
        Cookie cookie = new Cookie("REFRESH_TOKEN", "UUID");
        cookie.setMaxAge(123);
        cookie.setPath("/api/v1/auth/get-jwt");
        Mockito.when(authenticationService.authorizeAndCreateRefreshTokenCookie(any(LoginDto.class)))
                .thenReturn(cookie);

        String json = """
                {
                    "identifier": "exists",
                    "password": "does not match"
                }
                """;
        mockMvc.perform(
                        post("/api/v1/auth/get-refresh-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().value("REFRESH_TOKEN", "UUID"))
                .andExpect(MockMvcResultMatchers.cookie().maxAge("REFRESH_TOKEN", 123));
    }

    @Test
    void testGetJwt() throws Exception {
        Cookie cookie = new Cookie("REFRESH_TOKEN", "UUID");
        cookie.setMaxAge(123);
        cookie.setPath("/api/v1/auth/get-jwt");
        Mockito.when(authenticationService.refreshJwt("UUID"))
                .thenReturn(new JwtDto("token"));

        String resultJson = mockMvc.perform(
            post("/api/v1/auth/get-jwt")
                .cookie(cookie)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse()
        .getContentAsString();


        JacksonJsonParser jacksonJsonParser = new JacksonJsonParser();
        Map<String, Object> json = jacksonJsonParser.parseMap(resultJson);

        assertEquals(1, json.size());
        assertEquals("token", json.get("jwtToken"));
    }

    @Test
    void testGetJwtWithNoCookie() throws Exception {
        mockMvc.perform(post("/api/v1/auth/get-jwt"))
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.status().reason("Required cookie 'REFRESH_TOKEN' is not present."));
    }


    @Test
    void testGetJwtWithMalformedToken() throws Exception {
        Cookie cookie = new Cookie("REFRESH_TOKEN", "not-a-uuid-lol");
        cookie.setMaxAge(123);
        cookie.setPath("/api/v1/auth/get-jwt");
        Mockito.when(authenticationService.refreshJwt("not-a-uuid-lol"))
                .thenThrow(new HttpAuthExceptions.MalformedRefreshTokenId401());

        mockMvc.perform(
                    post("/api/v1/auth/get-jwt")
                            .cookie(cookie)
                )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.status().reason("MALFORMED_REFRESH_TOKEN"));
    }
}
