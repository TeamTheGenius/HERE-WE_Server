package com.genius.herewe.business.invitation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record InvitationInfo(
	String token,
	LocalDateTime invitedAt,
	LocalDateTime expiredAt
) {

	public static InvitationInfo create(int duration) {
		LocalDateTime now = LocalDateTime.now();
		String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		return InvitationInfo.builder()
			.invitedAt(now)
			.expiredAt(now.plusDays(duration))
			.token(uuid)
			.build();
	}
}
