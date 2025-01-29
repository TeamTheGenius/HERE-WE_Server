package com.genius.herewe.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProviderInfo {
    KAKAO("kakao_account", "id", "email"),
    NAVER("response", "id", "email"),
    GOOGLE(null, "sub", "email");

    private final String attributeKey;
    private final String providerCode;
    private final String identifier;
}
