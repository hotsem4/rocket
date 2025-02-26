package com.rocket.domains.user.application.dto.request;

import com.rocket.domains.user.application.dto.common.AddressDTO;
import com.rocket.domains.user.domain.enums.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(
    @NotBlank(message = "이메일은 필수입니다.")
    String email,

    @Min(value = 0, message = "나이는 음수일 수 없습니다.")
    Integer age,

    // 업데이트 시 선택적 필드 (null 허용)
    Gender gender,

    // 업데이트 시 선택적 필드 (null 허용)
    AddressDTO address
) {}
