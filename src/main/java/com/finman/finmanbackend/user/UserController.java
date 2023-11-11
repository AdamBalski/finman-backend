package com.finman.finmanbackend.user;

import com.finman.finmanbackend.user.HttpUserExceptions.EmailTaken409;
import com.finman.finmanbackend.util.validator.HttpValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.finman.finmanbackend.user.HttpUserExceptions.IdNotFound404;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/get-user/{uuid}")
    @ResponseBody
    public User getUser(@PathVariable("uuid") String uuidString) throws IdNotFound404, HttpValidationException {
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidString);
        } catch(Exception e) {
            throw new HttpValidationException(UUID.class);
        }
        return userService.getById(uuid);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/sign-up")
    public void signUp(@RequestBody UserDto userDto) throws EmailTaken409, HttpValidationException {
        userService.put(userDto);
    }
}
