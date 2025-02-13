package com.genius.herewe.core.security.dto.impl;

import static com.genius.herewe.core.user.domain.ProviderInfo.GOOGLE;

import com.genius.herewe.core.security.dto.OAuth2UserInfo;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

	public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getEmail() {
		return String.valueOf(attributes.get(GOOGLE.getEmail()));
	}

	@Override
	public String getProfileImage() {
		return String.valueOf(attributes.get(GOOGLE.getProfileImage()));
	}
}
