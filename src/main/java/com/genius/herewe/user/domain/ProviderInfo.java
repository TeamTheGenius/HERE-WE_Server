package com.genius.herewe.user.domain;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProviderInfo {
    KAKAO(List.of("kakao_account", "properties"), "email", "profile_image"),
    NAVER(List.of("response"), "email", "profile_image"),
    GOOGLE(null, "email", "picture");

    private final List<String> attributeKey;
    private final String email;
    private final String profileImage;

    public static ProviderInfo from(String registrationId) {
        return Arrays.stream(ProviderInfo.values())
                .filter(provider -> provider.name().equals(registrationId.toUpperCase()))
                .findFirst()
                .orElseThrow();
    }
}
