package com.genius.herewe.business.moment.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OptimisticLock;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.location.domain.Location;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Moment extends BaseTimeEntity implements FileHolder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "moment_id")
	private Long id;

	@Version
	private Long version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_id")
	private Crew crew;

	@OneToMany(mappedBy = "moment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MomentMember> momentMembers = new ArrayList<>();

	@OneToMany(mappedBy = "moment", cascade = CascadeType.ALL, orphanRemoval = true)
	@OptimisticLock(excluded = false)
	private List<Location> locations = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "files_id")
	private Files files;

	@Column(nullable = false)
	private String name;

	@ColumnDefault("0")
	private int participantCount;

	private Integer capacity;

	private LocalDateTime meetAt;

	private LocalDateTime closedAt;

	@Builder
	public Moment(String name, int participantCount, int capacity, LocalDateTime meetAt, LocalDateTime closedAt) {
		this.name = name;
		this.participantCount = participantCount;
		this.capacity = capacity;
		this.meetAt = meetAt;
		this.closedAt = closedAt;
	}

	//== 연관관계 편의 메서드 ==//
	public void addCrew(Crew crew) {
		this.crew = crew;
		if (!crew.getMoments().contains(this)) {
			crew.getMoments().add(this);
		}
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

	public void updateName(String name) {
		this.name = name;
	}

	public void updateCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public void updateMeetAt(LocalDateTime meetAt) {
		this.meetAt = meetAt;
	}

	public void updateClosedAt(LocalDateTime closedAt) {
		this.closedAt = closedAt;
	}

	public void updateLastModifiedTime() {
		this.modifiedAt = LocalDateTime.now();
	}
}
