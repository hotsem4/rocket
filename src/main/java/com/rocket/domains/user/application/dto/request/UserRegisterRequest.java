package com.rocket.domains.user.application.dto.request;

import com.rocket.domains.user.domain.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
    @NotBlank(message = "이메일 값이 비어 있습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "비밀번호 값이 비어 있을 수 없습니다.")
    @Size(min = 6, message = "비밀번호는 최소 6글자 이상이어야 합니다.")
    String password,

    @NotBlank(message = "나이 값이 비어있을 수 없습니다.")
    String age,

    @NotNull(message = "Gender 값이 null일 수 없습니다.")
    Gender gender,

    @NotNull(message = "Address 값이 null일 수 없습니다.")
    @Valid
    AddressRequest address,

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(max = 30, message = "닉네임은 최대 30자까지 입력 가능합니다.")
    String nickname
) {

}
