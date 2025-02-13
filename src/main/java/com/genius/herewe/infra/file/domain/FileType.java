package com.genius.herewe.infra.file.domain;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.Arrays;

import com.genius.herewe.core.global.exception.BusinessException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
	PROFILE("profile/"),
	CREW("crew/"),
	MOMENT("moment/");

	private final String path;

	public static FileType findType(String targetType) {
		String lowerTargetType = targetType.toUpperCase();

		return Arrays.stream(FileType.values())
			.filter(type -> type.name().equals(lowerTargetType))
			.findFirst()
			.orElseThrow(() -> new BusinessException(NOT_SUPPORTED_IMAGE_TYPE));
	}
}
