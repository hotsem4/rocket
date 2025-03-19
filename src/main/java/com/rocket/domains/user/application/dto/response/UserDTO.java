package com.rocket.domains.user.application.dto.response;

import com.rocket.domains.user.domain.entity.Address;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserDTO(
    @NotNull(message = "ID는 필수이며, 0보다 커야 합니다.")
    @Positive(message = "ID는 필수이며, 0보다 커야 합니다.")
    Long id,

    @NotBlank(message = "이메일 값이 비어 있을 수 없습니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @Min(value = 0, message = "나이는 음수일 수 없습니다.")
    int age,

    @NotNull(message = "성별 값은 null일 수 없습니다.")
    Gender gender,

    @NotNull(message = "주소 값이 null일 수 없습니다.")
    @Valid
    Address address
) {
  public static UserDTO fromUser(User user) {
    return new UserDTO(
        user.getId(),
        user.getEmail(),
        user.getAge(),
        user.getGender(),
        user.getAddress()
    );
  }
}
