package com.genius.herewe.core.user.domain;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProviderInfo {
	KAKAO(List.of("kakao_account", "properties"), "email", "profile_image", "kakao_"),
	NAVER(List.of("response"), "email", "profile_image", "naver_"),
	GOOGLE(null, "email", "picture", "google_");

	private final List<String> attributeKey;
	private final String email;
	private final String profileImage;
	private final String profilePrefix;

	public static ProviderInfo from(String registrationId) {
		String upperRegistrationId = registrationId.toUpperCase();

		return Arrays.stream(ProviderInfo.values())
			.filter(provider -> provider.name().equals(upperRegistrationId))
			.findFirst()
			.orElseThrow();
	}
}
