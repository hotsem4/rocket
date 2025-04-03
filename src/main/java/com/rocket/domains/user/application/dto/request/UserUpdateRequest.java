package com.rocket.domains.user.application.dto.request;

import com.rocket.domains.user.domain.enums.Gender;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    String email, // 포함!
    Integer age,
    Gender gender,
    AddressRequest address,
    @Size(max = 30, message = "닉네임은 최대 30자까지 입력 가능합니다.")
    String nickname,
    String phoneNumber
) {

  public UserUpdateRequest withEmail(String email) {
    return new UserUpdateRequest(email, age, gender, address, nickname, phoneNumber);
  }
}