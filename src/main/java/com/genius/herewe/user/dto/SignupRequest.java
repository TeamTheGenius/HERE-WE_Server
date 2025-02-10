package com.genius.herewe.user.dto;

public record SignupRequest(
	Long userId,
	String nickname
) {
}
