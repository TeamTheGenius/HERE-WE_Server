package com.genius.herewe.business.crew.domain;

import java.time.LocalDate;

import com.genius.herewe.core.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class CrewMember {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crew_member_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_id")
	private Crew crew;

	@Enumerated(value = EnumType.STRING)
	private CrewRole role;

	private LocalDate joinedAt;

	@Builder
	public CrewMember(CrewRole role, LocalDate joinedAt) {
		this.role = role;
		this.joinedAt = joinedAt;
	}

	public static CrewMember createByRole(CrewRole role) {
		return new CrewMember(role, LocalDate.now());
	}

	//== 연관관계 편의 메서드 ==//
	public void joinCrew(User user, Crew crew) {
		this.user = user;
		this.crew = crew;
		if (!user.getCrewMembers().contains(this)) {
			user.getCrewMembers().add(this);
		}
		if (!crew.getCrewMembers().contains(this)) {
			crew.updateParticipantCount(1);
			crew.getCrewMembers().add(this);
		}
	}
}
