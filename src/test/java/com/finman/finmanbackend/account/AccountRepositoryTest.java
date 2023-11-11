package com.finman.finmanbackend.account;

import com.finman.finmanbackend.user.User;
import com.finman.finmanbackend.user.UserRepository;
import com.finman.finmanbackend.user.UserRole;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Testcontainers
@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {
    @Rule
    public final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:12.7"));

    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;

    private User user1;
    private Account account1;

    @BeforeEach
    void init() {
        user1 = userRepository.saveAndFlush(new User(UUID.randomUUID(), "email", "pass", UserRole.ADMIN));
        account1 = accountRepository.saveAndFlush(new Account(UUID.randomUUID(), user1, "acc1"));
    }

    @Test
    void testUserIsNonNullable() {
        Executable exe = () -> accountRepository.save(new Account(UUID.randomUUID(), null, "name"));
        assertThrows(DataIntegrityViolationException.class, exe);
    }

    @Test
    void testUserIsNotUnique() {
        Executable exe = () -> accountRepository.save(new Account(UUID.randomUUID(), user1, "name"));
        assertDoesNotThrow(exe);
    }

    @Test
    void testNameIsNonNullable() {
        Executable exe = () -> accountRepository.save(new Account(UUID.randomUUID(), user1, null));
        assertThrows(DataIntegrityViolationException.class, exe);
    }

    @Test
    void testNameIsNotUnique() {
        Executable exe = () -> accountRepository.save(new Account(UUID.randomUUID(), user1, account1.getName()));
        assertDoesNotThrow(exe);
    }

    @Test
    void testFindByUserMail() {
        List<Account> fetched = accountRepository.findByUserEmail(user1.getEmail());
        assertEquals(1, fetched.size());
        assertEquals(user1, fetched.get(0).getUser());
        assertEquals(account1, fetched.get(0));
    }

    @Test
    void testFindByUserMailAndEnsureReturnsOnlyThisUsersRepositories() {
        User user2 = userRepository.saveAndFlush(new User(null, "email2", "pass2", UserRole.ADMIN));
        Account account2 = accountRepository.saveAndFlush(new Account(null, user2, "name2"));

        List<Account> fetched = accountRepository.findByUserEmail(user2.getEmail());
        assertEquals(1, fetched.size());
        assertEquals(user2, fetched.get(0).getUser());
        assertEquals(account2, fetched.get(0));
    }

    @Test
    void testFindByUserMailWhenNoSuchUser() {
        assertEquals(List.of(), accountRepository.findByUserEmail("email_nonexistent"));
    }
}