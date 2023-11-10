package com.finman.finmanbackend.user;

import com.finman.finmanbackend.user.HttpUserExceptions.IdNotFound404;
import com.finman.finmanbackend.util.validator.HttpValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    void put(UserDto userDto) throws HttpValidationException {
        UserDtoValidationResult userDtoValidationResult = UserDtoValidator.validate(userDto);
        if(userDtoValidationResult.isFailure()) {
            throw new HttpValidationException(userDtoValidationResult, UserDto.class);
        }

        User user = User.valueOf(userDto, passwordEncoder);
        try {
            userRepository.save(user);
        } catch(DataIntegrityViolationException e) {
            throw new HttpUserExceptions.EmailTaken409();
        }
    }

    User getById(UUID uuid) {
        return userRepository
                .findById(uuid)
                .orElseThrow(IdNotFound404::new);
    }
}
