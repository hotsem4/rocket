package com.rocket.commons.security.service;

import com.rocket.commons.security.CustomUserDetails;
import com.rocket.domains.user.domain.repository.UserReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserReader userReader;


  public CustomUserDetailsService(@Lazy UserReader userReader) {
    this.userReader = userReader;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userReader.findByEmail(email)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException(email));
  }
}
