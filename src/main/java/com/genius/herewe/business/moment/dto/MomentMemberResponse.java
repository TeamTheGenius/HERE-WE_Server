package com.genius.herewe.business.moment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "모먼트에 참여한 사용자들의 정보를 담은 응답 객체")
public record MomentMemberResponse(
	@Schema(description = "사용자의 식별자(PK)", example = "1")
	Long userId,
	@Schema(description = "사용자의 닉네임", example = "도토리")
	String name
) {
}
