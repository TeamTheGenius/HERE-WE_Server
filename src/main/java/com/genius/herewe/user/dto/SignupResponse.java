package com.genius.herewe.user.dto;

import com.genius.herewe.file.dto.FileResponse;

public record SignupResponse(
	Long userId,
	String nickname,
	FileResponse fileResponse
) {
}
