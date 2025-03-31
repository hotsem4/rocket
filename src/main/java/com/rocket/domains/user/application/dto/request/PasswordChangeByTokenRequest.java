package com.rocket.domains.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PasswordChangeByTokenRequest(
    @Schema(description = "암호화된 resetToken", example = "asdkjasd9834-sdf98asf-...")
    String resetToken,

    @Schema(description = "새로운 비밀번호", example = "newSecurePassword123!")
    String newPassword
) {}
