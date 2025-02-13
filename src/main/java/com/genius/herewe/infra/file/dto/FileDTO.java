package com.genius.herewe.infra.file.dto;

import com.genius.herewe.infra.file.domain.FileEnv;
import com.genius.herewe.infra.file.domain.FileType;
import com.genius.herewe.infra.file.domain.Files;

import lombok.Builder;

@Builder
public record FileDTO(
	FileEnv environment,
	FileType fileType,
	String originalName,
	String storedName,
	String fileURI
) {

	public static FileDTO create(Files files) {
		if (files == null) {
			return FileDTO.builder()
				.environment(null)
				.fileType(null)
				.originalName("")
				.storedName("")
				.fileURI("")
				.build();
		}
		return FileDTO.builder()
			.environment(files.getEnvironment())
			.fileType(files.getType())
			.originalName(files.getOriginalName())
			.storedName(files.getStoredName())
			.fileURI(files.getFileURI())
			.build();
	}
}
