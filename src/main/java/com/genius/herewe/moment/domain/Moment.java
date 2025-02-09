package com.genius.herewe.moment.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import com.genius.herewe.crew.domain.Crew;
import com.genius.herewe.file.domain.Files;
import com.genius.herewe.location.domain.Location;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Moment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "moment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_id")
	private Crew crew;

	@OneToMany(mappedBy = "moment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Location> locations = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "files_id")
	private Files files;

	@Column(nullable = false)
	private String name;

	@ColumnDefault("0")
	private int participantCount;

	private int capacity;

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
}
