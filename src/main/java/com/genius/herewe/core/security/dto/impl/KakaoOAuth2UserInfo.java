package com.genius.herewe.core.security.dto.impl;

import static com.genius.herewe.core.user.domain.ProviderInfo.KAKAO;

import com.genius.herewe.core.security.dto.OAuth2UserInfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
	private final String EMAIL_KEY;
	private final String PROFILE_KEY;

	public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);

		EMAIL_KEY = KAKAO.getAttributeKey().get(0);
		PROFILE_KEY = KAKAO.getAttributeKey().get(1);
	}

	@Override
	public String getEmail() {
		Map<String, Object> account = (Map<String, Object>)attributes.get(EMAIL_KEY);
		String email = String.valueOf(account.get(KAKAO.getEmail()));
		return email;
	}

	@Override
	public String getProfileImage() {
		Map<String, Object> properties = (Map<String, Object>)attributes.get(PROFILE_KEY);
		String profileImage = String.valueOf(properties.get(KAKAO.getProfileImage()));
		return profileImage;
	}
}
