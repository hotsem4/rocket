package com.rocket.domains.user.application.dto.request;

import com.rocket.domains.user.domain.enums.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    String email,

    @Min(value = 0, message = "나이는 음수일 수 없습니다.")
    Integer age,

    // 업데이트 시 선택적 필드 (null 허용)
    Gender gender,

    // 업데이트 시 선택적 필드 (null 허용)
    AddressRequest address,

    // 업데이트 시 선택적 필드 (null 허용)
    @Size(max = 30, message = "닉네임은 최대 30자까지 입력 가능합니다.")
    String nickname
) {

}
