package com.finman.finmanbackend.auth;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Models an HTTP response body of an auth SWT-fetching query.
 *
 * @see AuthenticationController
 * @author Adam Balski
 */
@AllArgsConstructor
@EqualsAndHashCode
public class JwtDto {
    String jwtToken;
}
