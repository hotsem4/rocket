package com.rocket.domains.posts.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostUpdateDTO(
    @NotBlank(message = "제목은 필수 입력값이며, 비어 있을 수 없습니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    String title,

    @NotBlank(message = "내용은 필수 입력값이며, 비어 있을 수 없습니다.")
    @Size(max = 5000, message = "내용은 최대 5000자까지 입력 가능합니다.")
    String content
) {}
