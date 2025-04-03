package com.rocket.commons.security.auth;

import com.rocket.domains.auth.domain.repository.AuthUserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final AuthUserReader authUserReader;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return authUserReader.getAuthUserByEmail(email)
        .map(
            authUser -> new CustomUserDetails(
                authUser.id(),
                authUser.email(),
                authUser.password(),
                authUser.role()
            )
        )
        .orElseThrow(() -> new UsernameNotFoundException(email));
  }
}
