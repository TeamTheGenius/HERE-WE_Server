package com.genius.herewe.business.moment.domain;

import java.time.LocalDate;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MomentMember {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "moment_member_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moment_id")
	private Moment moment;

	private LocalDate joinedAt;

	public MomentMember(LocalDate joinedAt) {
		this.joinedAt = joinedAt;
	}

	public static MomentMember create() {
		return new MomentMember(LocalDate.now());
	}

	public void joinMoment(User user, Moment moment) {
		this.user = user;
		this.moment = moment;
		if (!user.getMomentMembers().contains(this)) {
			user.getMomentMembers().add(this);
		}
		if (!moment.getMomentMembers().contains(this)) {
			moment.getMomentMembers().add(this);
		}
	}
}
