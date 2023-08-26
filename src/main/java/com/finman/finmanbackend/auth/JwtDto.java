package com.finman.finmanbackend.auth;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Models an HTTP response body of an auth SWT-fetching query.
 *
 * @see AuthenticationController
 * @author Adam Balski
 */
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class JwtDto {
    String jwtToken;
}
