package com.rocket.config;

import com.rocket.commons.security.JwtAuthenticationFilter;
import com.rocket.commons.security.JwtProvider;
import com.rocket.commons.security.JwtResolver;
import com.rocket.commons.security.service.CustomUserDetailsService;
import com.rocket.domains.auth.domain.repository.RefreshTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtProvider jwtProvider;
  private final JwtResolver jwtResolver;
  private final RefreshTokenStore redis;
  private final CustomUserDetailsService customUserDetailsService;
  private final PasswordEncoder passwordEncoder;

  public SecurityConfig(JwtProvider jwtProvider, JwtResolver jwtResolver, RefreshTokenStore redis,
      CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
    this.jwtProvider = jwtProvider;
    this.jwtResolver = jwtResolver;
    this.redis = redis;
    this.customUserDetailsService = customUserDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth
  ) throws Exception {
    auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }


  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      AuthenticationManager authenticationManager
  ) {
    return new JwtAuthenticationFilter(jwtProvider, jwtResolver, authenticationManager, redis);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      AuthenticationManager authenticationManager) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/auth/**",
                "/register/**",
                "/email/**"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter(authenticationManager),
            UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
