package com.rocket.domains.posts.application.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record PostDetailInfoResponse(
    @NotNull(message = "게시글 ID는 필수입니다.")
    @Positive(message = "게시글 ID는 0보다 커야 합니다.")
    Long id,

    @NotBlank(message = "제목은 필수 입력값이며, 비어 있을 수 없습니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    String title,

    @NotBlank(message = "내용은 필수 입력값이며, 비어 있을 수 없습니다.")
    @Size(max = 5000, message = "내용은 최대 5000자까지 입력 가능합니다.")
    String content,

    @NotNull(message = "작성자 ID는 필수입니다.")
    @Positive(message = "작성자 ID는 0보다 커야 합니다.")
    Long authorId,

    @NotBlank(message = "닉네임은 비어 있을 수 없습니다.")
    String nickname,

    @NotNull(message = "게시글 생성 시간은 필수 입력값입니다.")
    LocalDateTime createdAt,

    @Min(value = 0, message = "좋아요 수는 0 이상이어야 합니다.")
    int likeCount
) {

}
