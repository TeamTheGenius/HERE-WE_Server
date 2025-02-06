package com.genius.herewe.file.dto;

import com.genius.herewe.file.domain.FileEnv;
import com.genius.herewe.file.domain.FileType;

import lombok.Builder;

@Builder
public record FileDTO(
	FileEnv environment,
	FileType fileType,
	String originalName,
	String storedName,
	String fileURI
) {
}
