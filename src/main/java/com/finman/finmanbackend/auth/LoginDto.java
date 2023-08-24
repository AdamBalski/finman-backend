package com.finman.finmanbackend.auth;

/**
 * Models an HTTP request body of an authenticating query.
 *
 * @see com.finman.finmanbackend.user.User
 * @see AuthenticationController
 * @author Adam Balski
 */
public record LoginDto(String identifier, String password) { }
