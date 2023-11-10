package com.finman.finmanbackend.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/get-refresh-token")
    @PreAuthorize("permitAll")
    public void getRefreshToken(@RequestBody LoginDto loginDto, HttpServletResponse response) throws HttpAuthExceptions.WrongPassword401, HttpAuthExceptions.NoSuchUsername401{
        Cookie refreshCookie = authenticationService.authorizeAndCreateRefreshTokenCookie(loginDto);
        response.addCookie(refreshCookie);
    }

    @PostMapping(path = "/get-jwt")
    @PreAuthorize("permitAll()")
    public JwtDto refreshJwt(@CookieValue("REFRESH_TOKEN") String refreshTokenCookie) throws HttpAuthExceptions.NoSuchRefreshToken401{
        return authenticationService.refreshJwt(refreshTokenCookie);
    }
}
