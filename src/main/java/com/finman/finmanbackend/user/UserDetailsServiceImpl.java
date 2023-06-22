package com.finman.finmanbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Used to fetch {@link User} using its identifier (in this case e-mail)
 *
 * @see com.finman.finmanbackend.user.User
 * @see org.springframework.security.core.userdetails.User
 * @author AdamBalski
 */
@Component
@Qualifier("userRepositoryBasedUserDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findOneByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found exception."));
        return UserDetailsImpl.valueOf(user);
    }

    private record UserDetailsImpl(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) implements UserDetails {
        private static UserDetailsImpl valueOf(User user) {
            return new UserDetailsImpl(
                    user.getEmail(),
                    user.getPassword(),
                    user.getAuthorities()
            );
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}