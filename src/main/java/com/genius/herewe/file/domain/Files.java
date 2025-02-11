package com.genius.herewe.file.domain;

import com.genius.herewe.file.dto.FileDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "files")
public class Files {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "files_id")
	private Long id;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private FileEnv environment;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private FileType type;

	private String originalName;

	private String storedName;

	private String fileURI;

	@Builder
	public Files(FileEnv environment, FileType type, String originalName, String storedName, String fileURI) {
		this.environment = environment;
		this.type = type;
		this.originalName = originalName;
		this.storedName = storedName;
		this.fileURI = fileURI;
	}

	public static Files createDummy() {
		return Files.builder()
			.environment(null)
			.type(null)
			.originalName("")
			.storedName("")
			.fileURI("")
			.build();
	}

	//== 비지니스 로직 ==//
	public void updateFiles(FileDTO fileDTO) {
		this.originalName = fileDTO.originalName();
		this.storedName = fileDTO.storedName();
		this.fileURI = fileDTO.fileURI();
	}
}
