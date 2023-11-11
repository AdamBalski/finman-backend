package com.finman.finmanbackend.account;

import com.finman.finmanbackend.user.User;
import com.finman.finmanbackend.user.UserRepository;
import com.finman.finmanbackend.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AccountServiceTest {
    private AccountService accountService;
    private AccountRepository accountRepository;
    private UserRepository userRepository;

    @BeforeEach
    void init() {
        this.accountRepository = Mockito.mock(AccountRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        accountService = new AccountService(accountRepository, userRepository);
    }

    @Test
    void testGetAccounts() {
        List<Account> accounts = List.of();
        String email = "email_fooBar";
        when(accountRepository.findByUserEmail(email)).thenReturn(accounts);
        assertSame(accounts, accountService.getAccounts(email));
    }

    @Test
    void testPutIfUserExists() {
        String email = "user-mail";
        String accountName = "accountName";
        User user = new User(null, email, "pass", UserRole.STANDARD);

        when(userRepository.findOneByEmail(eq(email))).thenReturn(Optional.of(user));

        accountService.put(email, accountName);

        verify(accountRepository).save(eq(new Account(null, user, accountName)));
    }

    @Test
    void testPutIfUserDoesNotExist() {
        String email = "user-mail";
        String accountName = "accountName";

        when(userRepository.findOneByEmail(email)).thenReturn(Optional.empty());

        Executable executable = () -> accountService.put(email, accountName);
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, executable);
        assertTrue(responseStatusException.getStatusCode().is5xxServerError());

        verifyNoInteractions(accountRepository);;
    }

    @Test
    void testPutIfAccountNameTooLong() {
        String accountName = "1".repeat(101);

        Executable executable = () -> accountService.put("email", accountName);
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, executable);
        assertTrue(responseStatusException.getStatusCode().isSameCodeAs(HttpStatus.UNPROCESSABLE_ENTITY));
        assertEquals("ACCOUNT_NAME_TOO_LONG_INVALID", responseStatusException.getReason());

        verifyNoInteractions(accountRepository);
        verifyNoInteractions(userRepository);
    }

}