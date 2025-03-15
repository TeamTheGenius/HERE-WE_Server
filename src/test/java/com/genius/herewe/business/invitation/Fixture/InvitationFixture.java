package com.genius.herewe.business.invitation.Fixture;

import java.time.LocalDateTime;

import com.genius.herewe.business.invitation.domain.Invitation;

public class InvitationFixture {
	public static Invitation createDefault() {
		return builder()
			.build();
	}

	public static Invitation createExpired() {
		LocalDateTime now = LocalDateTime.now();
		return builder()
			.invitedAt(now.minusDays(4))
			.expiredAt(now.minusDays(2))
			.build();
	}

	public static InvitationBuilder builder() {
		return new InvitationBuilder();
	}

	public static class InvitationBuilder {
		private String token = "invitation UUID token";
		private LocalDateTime invitedAt = LocalDateTime.now();
		private LocalDateTime expiredAt = LocalDateTime.now().plusDays(2);

		public InvitationBuilder token(String token) {
			this.token = token;
			return this;
		}

		public InvitationBuilder invitedAt(LocalDateTime invitedAt) {
			this.invitedAt = invitedAt;
			return this;
		}

		public InvitationBuilder expiredAt(LocalDateTime expiredAt) {
			this.expiredAt = expiredAt;
			return this;
		}

		public Invitation build() {
			return Invitation.builder()
				.token(token)
				.invitedAt(invitedAt)
				.expiredAt(expiredAt)
				.build();
		}
	}
}
