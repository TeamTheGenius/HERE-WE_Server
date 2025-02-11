package com.genius.herewe.file.dto;

import com.genius.herewe.file.domain.FileEnv;
import com.genius.herewe.file.domain.Files;

import lombok.Builder;

@Builder
public record FileResponse(
	Long fileId,
	String source,
	FileEnv fileEnv
) {

	public static FileResponse create(Files files, String source) {
		return FileResponse.builder()
			.fileId(files.getId())
			.source(source)
			.fileEnv(files.getEnvironment())
			.build();
	}
}
