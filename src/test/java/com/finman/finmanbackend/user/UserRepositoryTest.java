package com.finman.finmanbackend.user;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Rule
    public final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:12.7"));

    @Autowired
    UserRepository userRepository;

    @Test
    void testFindOneByEmailWhenThereAreNoEntries() {
        assertTrue(userRepository.findOneByEmail("email").isEmpty());
    }

    @Test
    void testFindOneByEmailWhenThereIsNoSuchEntry() {
        userRepository.save(new User(
                UUID.randomUUID(),
                "email1",
                "password",
                UserRole.ADMIN
        ));

        assertTrue(userRepository.findOneByEmail("email2").isEmpty());
    }

    @Test
    void testFindOneByEmail() {
        userRepository.save(new User(
                UUID.randomUUID(),
                "email1",
                "password",
                UserRole.ADMIN
        ));

        Optional<User> email = userRepository.findOneByEmail("email1");
        assertTrue(email.isPresent());
        User user = email.get();

        assertEquals("email1", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testUniqueRules() {
        userRepository.saveAndFlush(new User(
                UUID.randomUUID(),
                "email",
                "password",
                UserRole.STANDARD
        ));

        // role and password don't have to be unique
        Executable executable = () -> userRepository.saveAndFlush(new User(
                UUID.randomUUID(),
                "email2",
                "password",
                UserRole.STANDARD
        ));
        assertDoesNotThrow(executable);

        // email has to be unique
        try {
            userRepository.saveAndFlush(new User(
                    UUID.randomUUID(),
                    "email2",
                    "password2",
                    UserRole.STANDARD
            ));
            Assertions.fail("Exception was not thrown but should.");;
        }
        catch(DataIntegrityViolationException e) {
            // correct
        } catch(Exception e) {
            fail("Incorrect exception was thrown, should throw: org.springframework.dao.DataIntegrityViolationException.class");
            System.out.println("Exception which was thrown.:");
            e.printStackTrace();
        }
    }

    @Test
    void testNullabilityRules() {
        // email
        Executable executable = () -> userRepository.saveAndFlush(new User(
                UUID.randomUUID(),
                null,
                "",
                UserRole.STANDARD
        ));
        assertThrows(DataIntegrityViolationException.class, executable);

        // password
        executable = () -> userRepository.saveAndFlush(new User(
                UUID.randomUUID(),
                "",
                null,
                UserRole.STANDARD
        ));
        assertThrows(DataIntegrityViolationException.class, executable);

        // role
        executable = () -> userRepository.saveAndFlush(new User(
                UUID.randomUUID(),
                "",
                "",
                null
        ));
        assertThrows(DataIntegrityViolationException.class, executable);

         // uuid - should not throw, should generate a random uuid, set it and save that entry
        executable = () -> userRepository.save(new User(
                null,
                "",
                "",
                UserRole.STANDARD
        ));
        assertDoesNotThrow(executable);
    }
}