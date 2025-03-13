package com.genius.herewe.infra.mail.dto;

import lombok.Builder;

@Builder
public record MailRequest(
	String receiverMail,
	String nickname,
	String crewName,
	String introduce,
	int memberCount,
	String inviteUrl
) {
}
