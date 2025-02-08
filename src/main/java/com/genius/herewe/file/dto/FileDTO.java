package com.genius.herewe.file.dto;

import com.genius.herewe.file.domain.FileEnv;
import com.genius.herewe.file.domain.FileType;
import com.genius.herewe.file.domain.Files;

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
		return FileDTO.builder()
			.environment(files.getEnvironment())
			.fileType(files.getType())
			.originalName(files.getOriginalName())
			.storedName(files.getStoredName())
			.fileURI(files.getFileURI())
			.build();
	}
}
