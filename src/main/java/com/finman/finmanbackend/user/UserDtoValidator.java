package com.finman.finmanbackend.user;

import com.finman.finmanbackend.util.validator.Validator;

import java.util.regex.Pattern;

import static com.finman.finmanbackend.user.UserDtoValidationResult.*;

public class UserDtoValidator {
    private static final Pattern passwordValidCharactersPattern = Pattern.compile("[a-zA-Z0-9-+!@#$%^&*()]*");
    private static final Pattern emaiLPattern = Pattern.compile("[^@]+@[^@]+");

    private static final Validator<UserDto, UserDtoValidationResult> validateEmailNullability = userDto ->
            userDto.getEmail() != null ? VALID : EMAIL_NULL;
    private static final Validator<UserDto, UserDtoValidationResult> validatePasswordNullability = userDto ->
            userDto.getPassword() != null ? VALID : PASSWORD_NULL;
    private static final Validator<UserDto, UserDtoValidationResult> validatePasswordNotTooShort = userDto ->
            userDto.getPassword().length() >= 8 ? VALID : PASSWORD_TOO_SHORT;
    private static final Validator<UserDto, UserDtoValidationResult> validaEmailNotTooLong = userDto ->
            userDto.getEmail().length() <= 254 ? VALID : EMAIL_TOO_LONG;
    private static final Validator<UserDto, UserDtoValidationResult> validatePasswordsValidCharacters = userDto ->
            passwordValidCharactersPattern.matcher(userDto.getPassword()).matches() ? VALID : PASSWORD_INVALID_CHARACTERS;
    private static final Validator<UserDto, UserDtoValidationResult> validateEmailByRegex = userDto ->
            emaiLPattern.matcher(userDto.getEmail()).matches() ? VALID : EMAIL_NOT_EMAIL;

    public static UserDtoValidationResult validate(UserDto userDto) {
        return validateEmailNullability
                .and(validatePasswordNullability)
                .and(validatePasswordNotTooShort)
                .and(validatePasswordsValidCharacters)
                .and(validaEmailNotTooLong)
                .and(validateEmailByRegex)
                .validate(userDto);
    }
}
