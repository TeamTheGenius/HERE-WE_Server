package com.genius.herewe.business.crew.dto;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.domain.CrewRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "크루의 전체 정보를 담은 응답 객체")
public record CrewResponse(
	@Schema(description = "크루 엔티티의 식별자(PK)", example = "1")
	Long crewId,
	@Schema(description = "크루 이름", example = "눈물나게 맛있는거 먹고싶은 사람들")
	String name,
	@Schema(description = "크루 리더의 닉네임", example = "도토리")
	String leaderName,
	@Schema(description = "사용자의 크루 내의 역할", example = "LEADER  or  MEMBER")
	CrewRole role,
	@Schema(description = "크루 설명글", example = "수도권의 맛집들을 뽀개는 동아리입니다!")
	String introduce,
	@Schema(description = "현재까지 참여한 인원 수", example = "13")
	int participantCount
) {

	public static CrewResponse create(Crew crew, CrewRole role) {
		return CrewResponse.builder()
			.crewId(crew.getId())
			.name(crew.getName())
			.leaderName(crew.getLeaderName())
			.role(role)
			.introduce(crew.getIntroduce())
			.participantCount(crew.getParticipantCount())
			.build();
	}
}
