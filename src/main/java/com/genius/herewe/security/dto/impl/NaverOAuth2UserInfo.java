package com.genius.herewe.security.dto.impl;

import static com.genius.herewe.user.domain.ProviderInfo.NAVER;

import com.genius.herewe.security.dto.OAuth2UserInfo;
import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {
    private final Map<String, Object> responses;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        responses = (Map<String, Object>) attributes.get(NAVER.getAttributeKey().get(0));
    }

    @Override
    public String getEmail() {
        return String.valueOf(responses.get(NAVER.getEmail()));
    }

    @Override
    public String getProfileImage() {
        return String.valueOf(responses.get(NAVER.getProfileImage()));
    }
}
