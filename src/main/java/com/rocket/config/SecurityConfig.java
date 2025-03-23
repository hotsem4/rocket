package com.rocket.config;

import com.rocket.commons.security.JwtAuthenticationFilter;
import com.rocket.commons.security.JwtProvider;
import com.rocket.commons.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtProvider jwtProvider;

  public SecurityConfig(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      ObjectProvider<CustomUserDetailsService> userDetailsServiceProvider) {
    return new JwtAuthenticationFilter(jwtProvider, userDetailsServiceProvider);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      ObjectProvider<CustomUserDetailsService> userDetailsServiceProvider) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter(userDetailsServiceProvider),
            UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
