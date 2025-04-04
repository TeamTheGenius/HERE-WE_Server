package com.genius.herewe.core.global.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {
	@LastModifiedDate
	@Column(name = "modified_at")
	protected LocalDateTime modifiedAt;
	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
}
