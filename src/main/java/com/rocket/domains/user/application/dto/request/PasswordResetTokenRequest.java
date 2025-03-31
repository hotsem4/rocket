package com.rocket.domains.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PasswordResetTokenRequest(
    @Schema(description = "사용자 이메일", example = "user@example.com")
    String email,

    @Schema(description = "사용자 전화번호", example = "01012345678")
    String phoneNumber
) {}
