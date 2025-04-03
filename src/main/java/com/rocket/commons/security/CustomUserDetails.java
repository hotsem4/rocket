package com.rocket.commons.security;

import com.rocket.domains.user.domain.enums.Role;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record CustomUserDetails(Long id, String email, String password, Role role) implements UserDetails {

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> "ROLE_" + role.name());
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  public boolean hasRole(String roleName) {
    return role.name().equalsIgnoreCase(roleName);
  }
}
