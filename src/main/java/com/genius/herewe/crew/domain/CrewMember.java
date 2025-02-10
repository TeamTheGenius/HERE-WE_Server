package com.genius.herewe.crew.domain;

import java.time.LocalDate;

import com.genius.herewe.user.domain.User;

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
}
