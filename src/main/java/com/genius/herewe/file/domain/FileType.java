package com.genius.herewe.file.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
	PROFILE("profile/"),
	CREW("crew/"),
	MOMENT("moment/");

	private final String path;
}
