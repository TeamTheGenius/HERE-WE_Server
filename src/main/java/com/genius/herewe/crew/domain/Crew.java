package com.genius.herewe.crew.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import com.genius.herewe.chat.domain.ChatRoom;
import com.genius.herewe.file.domain.Files;
import com.genius.herewe.notice.domain.Notice;

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
public class Crew {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crew_id")
	private Long id;

	@OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notice> notices = new ArrayList<>();

	@OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CrewMember> crewMembers = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatRoom;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "files_id")
	private Files files;

	@Column(nullable = false)
	private String leaderName;

	@Column(unique = true, length = 20)
	private String name;

	@Column(columnDefinition = "TEXT", length = 1000)
	private String introduce;

	@ColumnDefault("0")
	private int participantCount;

	private LocalDateTime createdAt;

	@Builder
	public Crew(String leaderName, String name, String introduce, int participantCount, LocalDateTime createdAt) {
		this.leaderName = leaderName;
		this.name = name;
		this.introduce = introduce;
		this.participantCount = participantCount;
		this.createdAt = createdAt;
	}
}
