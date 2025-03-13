package com.genius.herewe.business.invitation.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.core.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_id")
	private Crew crew;

	@Column(unique = true, nullable = false)
	private String token;

	private LocalDateTime invitedAt;

	@Column(nullable = false)
	private LocalDateTime expiredAt;

	@Builder
	public Invitation(LocalDateTime invitedAt, LocalDateTime expiredAt, String token) {
		this.expiredAt = expiredAt;
		this.invitedAt = invitedAt;
		this.token = token;
	}

	public static Invitation create(long expirationDays) {
		LocalDateTime now = LocalDateTime.now();
		String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		return Invitation.builder()
			.invitedAt(now)
			.expiredAt(now.plusDays(expirationDays))
			.token(uuid)
			.build();
	}

	//== 연관관계 편의 메서드 ==//
	public void inviteUser(User user, Crew crew) {
		this.user = user;
		this.crew = crew;
		if (!user.getInvitations().contains(this)) {
			user.getInvitations().add(this);
		}
		if (!crew.getInvitations().contains(this)) {
			crew.getInvitations().add(this);
		}
	}

	//== 비지니스 로직 ==//
	public void update(Invitation updated) {
		this.token = updated.getToken();
		this.invitedAt = updated.getInvitedAt();
		this.expiredAt = updated.getExpiredAt();
	}
}
