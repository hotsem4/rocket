package com.rocket.commons.security;

import com.rocket.domains.auth.domain.repository.RefreshTokenStore;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final JwtResolver jwtResolver;
  //  private final CustomUserDetailsService userDetailsService;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenStore redis;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    String accessToken = jwtResolver.resolveAccessToken(request.getHeader("Authorization"));

    if (accessToken != null) {
      if (jwtProvider.validateToken(accessToken)) {
        // 정상 accessToken
        authenticateWithEmail(jwtProvider.getEmail(accessToken));
      } else if (jwtProvider.isExpired(accessToken)) {
        // AccessToken 만료 -> RefreshToken 검사
        String refreshToken = jwtResolver.resolveRefreshToken(request.getHeader("X-Refresh-Token"));

        if (refreshToken != null && jwtProvider.validateToken(refreshToken)
            && jwtProvider.isRefreshTokenValid(refreshToken)) {
          String email = jwtProvider.getEmail(refreshToken);
          String savedToken = redis.get(email);

          if (refreshToken.equals(savedToken)) {
            // 새 AccessToken 발급
            String newAccessToken = jwtProvider.createAccessToken(email);
            response.setHeader("X-New-Access-Token", newAccessToken); // 클라이언트가 받아서 저장할수 있도록

            // 인증 처리
            authenticateWithEmail(email);
          }
        }
      }
    }

    filterChain.doFilter(request, response);
  }

  private void authenticateWithEmail(String email) {

    // PreAuthenticatedAuthenticationToken을 사용하여 AuthenticationManager에 위임
    PreAuthenticatedAuthenticationToken authRequest =
        new PreAuthenticatedAuthenticationToken(email, null);
    Authentication authentication = authenticationManager.authenticate(authRequest);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
