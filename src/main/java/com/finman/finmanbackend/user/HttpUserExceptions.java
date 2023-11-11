package com.finman.finmanbackend.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class HttpUserExceptions {
    private HttpUserExceptions() {}
    public static class IdNotFound404 extends ResponseStatusException {
        public IdNotFound404() {
            super(HttpStatus.NOT_FOUND, "ID_NOT_FOUND");
        }
    }

    public static class EmailTaken409 extends ResponseStatusException {
        public EmailTaken409() {
            super(HttpStatus.CONFLICT, "EMAIL_TAKEN");
        }
    }
}
