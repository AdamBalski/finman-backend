package com.finman.finmanbackend.user;

import org.springframework.security.core.GrantedAuthority;

/**
 * Determines whether {@link User} has normal or elevated privileges.
 *
 * @see GrantedAuthority
 * @see User
 * @author AdamBalski
 */
public enum UserRole {
    STANDARD("ROLE_STANDARD"), ADMIN("ROLE_ADMIN");

    private final String grantedAuthorityString;
    private final GrantedAuthority grantedAuthority;

    UserRole(String grantedAuthorityString) {
        this.grantedAuthorityString = grantedAuthorityString;
        this.grantedAuthority = new UserRoleGrantedAuthoritiesImpl(this);
    }

    public GrantedAuthority getGrantedAuthority() {
        return this.grantedAuthority;
    }

    private record UserRoleGrantedAuthoritiesImpl(UserRole role) implements GrantedAuthority {
        @Override
            public String getAuthority() {
                return role.grantedAuthorityString;
            }
        }
}
