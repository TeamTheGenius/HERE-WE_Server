package com.genius.herewe.business.crew.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import com.genius.herewe.business.chat.domain.ChatRoom;
import com.genius.herewe.business.invitation.domain.Invitation;
import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.notice.domain.Notice;
import com.genius.herewe.core.global.domain.BaseTimeEntity;
import com.genius.herewe.infra.file.domain.FileHolder;
import com.genius.herewe.infra.file.domain.Files;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crew extends BaseTimeEntity implements FileHolder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crew_id")
	private Long id;

	@OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notice> notices = new ArrayList<>();

	@OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CrewMember> crewMembers = new ArrayList<>();

	@OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Moment> moments = new ArrayList<>();

	@OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Invitation> invitations = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatRoom;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "files_id")
	private Files files;

	@Column(nullable = false)
	private String leaderName;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(columnDefinition = "TEXT", length = 1000)
	private String introduce;

	@ColumnDefault("0")
	private int participantCount;

	@Builder
	public Crew(Long id, String leaderName, String name, String introduce, int participantCount) {
		this.id = id;
		this.leaderName = leaderName;
		this.name = name;
		this.introduce = introduce;
		this.participantCount = participantCount;
	}

	@Override
	public Files getFiles() {
		if (files == null) {
			return Files.createDummy();
		}
		return this.files;
	}

	//== 비지니스 로직 ==//
	@Override
	public void setFiles(Files files) {
		this.files = files;
	}

	public void modify(String name, String introduce) {
		if (name != null)
			this.name = name;
		if (introduce != null)
			this.introduce = introduce;
	}

	public void updateParticipantCount(int amount) {
		if (this.participantCount != crewMembers.size()) {
			this.participantCount = crewMembers.size();
		}
		if (amount < 0 && this.participantCount + amount < 0) {
			return;
		}
		this.participantCount += amount;
	}
}
