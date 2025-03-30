package com.rocket.commons.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rocket.commons.security.CustomUserDetails;
import com.rocket.domains.auth.domain.entity.AuthUser;
import com.rocket.domains.auth.domain.repository.AuthUserReader;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomUserDetailsServiceUnitTest {

  private AuthUserReader authUserReader;
  private CustomUserDetailsService customUserDetailsService;

  @BeforeEach
  void setUp() {
    authUserReader = mock(AuthUserReader.class);
    customUserDetailsService = new CustomUserDetailsService(authUserReader);
  }

  @Test
  @DisplayName("이메일로 사용자 정보를 성공적으로 로드할 수 있다.")
  void loadUserByUsernameSuccess() {
    // given
    String email = "test@rocket.com";
    String password = "encodedPassword";
    Long userId = 1L;

    AuthUser authUser = new AuthUser(userId, email, password);
    when(authUserReader.getAuthUserByEmail(email)).thenReturn(Optional.of(authUser));

    // when
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

    // then
    assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
    CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
    assertThat(customUserDetails.email()).isEqualTo(email);
    assertThat(customUserDetails.getPassword()).isEqualTo(password);
    assertThat(customUserDetails.id()).isEqualTo(userId);
  }

  @Test
  @DisplayName("사용자를 찾을 수 없을 경우 UsernameNotFoundException 예외를 던진다.")
  void loadUserByUsernameThrowsException() {
    // given
    String email = "notfound@rocket.com";
    when(authUserReader.getAuthUserByEmail(email)).thenReturn(Optional.empty());

    // expect
    assertThrows(UsernameNotFoundException.class, () ->
        customUserDetailsService.loadUserByUsername(email));
  }

  @DisplayName("null 이메일을 전달하면 UsernameNotFoundException을 던진다")
  @Test
  void loadUserByUsernameWithNullEmail() {
    assertThrows(UsernameNotFoundException.class, () -> {
      customUserDetailsService.loadUserByUsername(null);
    });
  }

  @DisplayName("AuthUser의 필드가 일부 null이더라도 CustomUserDetails는 생성된다")
  @Test
  void loadUserByUsernameWithIncompleteAuthUser() {
    String email = "incomplete@rocket.com";
    AuthUser authUser = new AuthUser(null, email, null); // ID, password가 null
    when(authUserReader.getAuthUserByEmail(email)).thenReturn(Optional.of(authUser));

    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

    assertThat(userDetails.getUsername()).isEqualTo(email);
    assertThat(userDetails.getPassword()).isNull();
  }


}
