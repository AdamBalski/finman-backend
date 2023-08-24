package com.finman.finmanbackend.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Throwing an inner exception in a controller, will
 * automatically return an HTTP response with a given
 * error code.
 *
 * @see org.springframework.web.bind.annotation.RestController
 * @author Adam Balski
 */
public abstract class HttpExceptions {
    public static class NoSuchUsername401 extends ResponseStatusException {
        public NoSuchUsername401() {
            super(HttpStatus.UNAUTHORIZED, "NO_SUCH_USERNAME");
        }
    }

    public static class WrongPassword401 extends ResponseStatusException {
        public WrongPassword401() {
            super(HttpStatus.UNAUTHORIZED, "WRONG_PASSWORD");
        }
    }

    public static class NoSuchRefreshToken401 extends ResponseStatusException {
        public NoSuchRefreshToken401() {
            super(HttpStatus.UNAUTHORIZED, "NO_SUCH_REFRESH_TOKEN");
        }
    }

    public static class MalformedRefreshTokenId401 extends ResponseStatusException {
        public MalformedRefreshTokenId401() {
            super(HttpStatus.UNAUTHORIZED, "MALFORMED_REFRESH_TOKEN");
        }
    }
}
