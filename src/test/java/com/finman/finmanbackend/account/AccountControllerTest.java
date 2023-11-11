package com.finman.finmanbackend.account;

import com.finman.finmanbackend.auth.AuthenticationController;
import com.finman.finmanbackend.auth.AuthenticationService;
import com.finman.finmanbackend.security.SecurityConfig;
import com.finman.finmanbackend.security.jwt.JwtFilter;
import com.finman.finmanbackend.security.jwt.JwtUtil;
import com.finman.finmanbackend.user.User;
import com.finman.finmanbackend.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class)
@Import({SecurityConfig.class})
class AccountControllerTest {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    @Qualifier("userRepositoryBasedUserDetailsServiceImpl")
    UserDetailsService userDetailsService;

    @BeforeEach
    void init() {

    }

    @WithAnonymousUser
    @Test
    void testGetAccountsWithAnonymousUser() throws Exception {
        mockMvc.perform(
                get("/api/v1/account/get-accounts")
        )
                .andExpect(status().isUnauthorized());
    }


    @WithMockUser(username = "emailEmail", roles = "STANDARD")
    @Test
    void testGetAccounts() throws Exception {
        UUID accountId = UUID.fromString("4ad03786-f2dd-4d6f-8383-74ff0b88fd60");
        UUID userId = UUID.fromString("3e0ddda4-e766-42c1-8c4e-d219b847e627");
        List<Account> accounts = List.of(new Account(
                accountId,
                new User(userId, "email", "password", UserRole.ADMIN),
                "acc-name"
        ));
        when(accountService.getAccounts(eq("emailEmail"))).thenReturn(accounts);
        when(accountService.getAccounts(not(eq("emailEmail")))).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/v1/account/get-accounts")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(" [{\"id\":\"4ad03786-f2dd-4d6f-8383-74ff0b88fd60\",\"user\":\"3e0ddda4-e766-42c1-8c4e-d219b847e627\",\"name\":\"acc-name\"}]"))
                .andDo(MockMvcResultHandlers.print());

        verify(accountService).getAccounts(eq("emailEmail"));
        verifyNoMoreInteractions(accountService);
    }

    @WithAnonymousUser
    @Test
    void testPutWithAnonymousUser() throws Exception {
        String json = "acc-name";
        mockMvc.perform(
                        post("/api/v1/account/create-account")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "emailEmail", roles = "STANDARD")
    @Test
    void testPutWithNoRequestBody() throws Exception {
        mockMvc.perform(
                        post("/api/v1/account/create-account")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @WithMockUser(username = "emailEmail", roles = "STANDARD")
    @Test
    void testPut() throws Exception {
        String json = "acc-name";
        mockMvc.perform(
                        post("/api/v1/account/create-account")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk());

        verify(accountService).put("emailEmail", "acc-name");
    }
}