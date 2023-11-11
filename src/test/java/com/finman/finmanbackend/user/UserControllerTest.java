package com.finman.finmanbackend.user;

import com.finman.finmanbackend.security.SecurityConfig;
import com.finman.finmanbackend.security.jwt.JwtFilter;
import com.finman.finmanbackend.security.jwt.JwtUtil;
import com.finman.finmanbackend.util.validator.HttpValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(value = {SecurityConfig.class})
public class UserControllerTest {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    MockMvc mockMvc;

    // ApplicationContext wants repositories (services -> repositories),
    // to inject to services,
    // but springboot can't instantiate repositories with @WebMvcTest, so we
    // have to create mocked beans.
    @MockBean
    UserService userService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    @Qualifier("userRepositoryBasedUserDetailsServiceImpl")
    UserDetailsService userDetailsService;

    private User mockUser;

    @BeforeEach
    void init() {
        mockUser = new User(UUID.randomUUID(), "email@email", "noop_pass", UserRole.ADMIN);
        when(userService.getById(mockUser.getId())).thenReturn(mockUser);
        when(userService.getById(not(eq(mockUser.getId())))).thenThrow(new HttpUserExceptions.IdNotFound404());
    }

    @WithMockUser(roles = "STANDARD")
    @Test
    void testGetByIdWhenUuidIsInvalid() throws Exception {
        mockMvc.perform(
                        get("/api/v1/user/get-user/4371b356-c52b-4625-86db-a70be760")
                )
                .andExpect(status().isNotFound())
                .andExpect(status().reason("ID_NOT_FOUND"))
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetById() throws Exception {
        mockMvc
                .perform(get("/api/v1/user/get-user/" + mockUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("id").value(mockUser.getId().toString()))
                .andExpect(jsonPath("email").value(mockUser.getEmail()))
                .andExpect(jsonPath("role").value(mockUser.getRole().name()))
                .andDo(MockMvcResultHandlers.print());
    }

    @WithAnonymousUser
    @Test
    void testGetByIdWithAnonymousUser() throws Exception {
        mockMvc
                .perform(get("/api/v1/user/get-user/lol"))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetByIdWhenUserDoesNotExist() throws Exception {
        mockMvc
                .perform(get("/api/v1/user/get-user/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("ID_NOT_FOUND"))
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(MockMvcResultHandlers.print());
    }

    @WithAnonymousUser
    @Test
    void testSignUp() throws Exception {
        String json = """
                {"email": "email@email", "password": "password"}
        """;
        mockMvc.perform(
                post("/api/v1/user/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
        )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @WithMockUser(roles = "STANDARD")
    @Test
    void testSignUpWhenLoggedIn() throws Exception {
        UserDto userDto = new UserDto("email@email", "password");

        String json = """
                {"email": "email@email", "password": "password"}
        """;
        mockMvc.perform(
                        post("/api/v1/user/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isForbidden())
                .andDo(MockMvcResultHandlers.print());

        verify(userService, never()).put(userDto);
    }

    @WithAnonymousUser
    @Test
    void testSignUpWhenMailExists() throws Exception {
        UserDto userDto = new UserDto("email@email", "password");
        doThrow(new HttpUserExceptions.EmailTaken409())
                .when(userService).put(userDto);

        String json = """
                {"email": "email@email", "password": "password"}
        """;
        mockMvc.perform(
                        post("/api/v1/user/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isConflict())
                .andExpect(status().reason("EMAIL_TAKEN"))
                .andDo(MockMvcResultHandlers.print());

        verify(userService).put(userDto);
    }

    @WithAnonymousUser
    @Test
    void testSignUpWithTooShortPassword() throws Exception {
        UserDto userDto = new UserDto("email@email", "password");
        doThrow(new HttpValidationException(UserDtoValidationResult.PASSWORD_TOO_SHORT, UserDto.class))
                .when(userService).put(userDto);

        String json = """
                {"email": "email@email", "password": "password"}
        """;
        mockMvc.perform(
                        post("/api/v1/user/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isUnprocessableEntity())
                .andExpect(status().reason("com.finman.finmanbackend.user.UserDto_INVALID_PASSWORD_TOO_SHORT"))
                .andDo(MockMvcResultHandlers.print());
    }
}