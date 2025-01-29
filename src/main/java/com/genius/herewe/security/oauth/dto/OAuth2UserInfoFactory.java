package com.genius.herewe.security.oauth.dto;

import com.genius.herewe.security.oauth.dto.impl.GoogleOAuth2UserInfo;
import com.genius.herewe.security.oauth.dto.impl.KakaoOAuth2UserInfo;
import com.genius.herewe.security.oauth.dto.impl.NaverOAuth2UserInfo;
import com.genius.herewe.user.domain.ProviderInfo;
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
