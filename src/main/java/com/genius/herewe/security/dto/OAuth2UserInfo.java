package com.genius.herewe.security.dto;

import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public abstract String getEmail();

    public abstract String getProfileImage();
}
