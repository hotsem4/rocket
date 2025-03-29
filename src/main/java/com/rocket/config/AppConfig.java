package com.rocket.config;

import com.rocket.commons.security.service.CustomUserDetailsService;
import com.rocket.domains.auth.domain.repository.AuthUserReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CustomUserDetailsService customUserDetailsService(
      AuthUserReader authUserReader) {
    return new CustomUserDetailsService(authUserReader);
  }
}
