package com.genius.herewe.core.security.dto;

import com.genius.herewe.core.security.dto.impl.GoogleOAuth2UserInfo;
import com.genius.herewe.core.security.dto.impl.KakaoOAuth2UserInfo;
import com.genius.herewe.core.security.dto.impl.NaverOAuth2UserInfo;
import com.genius.herewe.core.user.domain.ProviderInfo;

import java.util.Map;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2UserInfoFactory {
	public static OAuth2UserInfo createUserInfo(ProviderInfo providerInfo, Map<String, Object> attributes) {
		switch (providerInfo) {
			case KAKAO -> {
				return new KakaoOAuth2UserInfo(attributes);
			}
			case NAVER -> {
				return new NaverOAuth2UserInfo(attributes);
			}
			case GOOGLE -> {
				return new GoogleOAuth2UserInfo(attributes);
			}
		}
		throw new OAuth2AuthenticationException("INVALID PROVIDER TYPE");
	}
}
