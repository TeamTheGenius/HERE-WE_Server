package com.genius.herewe.notice.domain;

import java.time.LocalDateTime;

import com.genius.herewe.crew.domain.Crew;

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
public class Notice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notice_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_id")
	private Crew crew;

	@Column(length = 30)
	private String title;

	@Column(columnDefinition = "TEXT", length = 500)
	private String content;

	private LocalDateTime modifiedAt; //auditing 추가 필요

	@Builder
	public Notice(String title, String content, LocalDateTime modifiedAt) {
		this.title = title;
		this.content = content;
		this.modifiedAt = modifiedAt;
	}
}
