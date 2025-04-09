package com.genius.herewe.business.invitation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "크루 초대 요청 DTO")
public record InvitationRequest(
	@Schema(description = "초대할 크루의 식별자(PK)", example = "1")
	Long crewId,
	@Schema(description = "크루 초대를 받을 사용자의 닉네임", example = "HEY")
	String nickname
) {
}
