package com.genius.herewe.business.crew.dto;

import java.time.LocalDate;

import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.domain.CrewRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "크루의 멤버탭에 필요한 정보를 담은 응답 객체")
public record CrewMemberResponse(
	@Schema(description = "사용자의 식별자(PK)", example = "1")
	Long userId,
	@Schema(description = "사용자의 닉네임", example = "도토리")
	String name,
	@Schema(description = "사용자의 크루 내 역할", example = "LEADER  or  MEMBER")
	CrewRole role,
	@Schema(description = "크루 참여일자", example = "2025.03.09")
	LocalDate joinedAt
) {

	public static CrewMemberResponse create(CrewMember crewMember) {
		return CrewMemberResponse.builder()
			.userId(crewMember.getUser().getId())
			.name(crewMember.getUser().getNickname())
			.role(crewMember.getRole())
			.joinedAt(crewMember.getJoinedAt())
			.build();
	}
}
