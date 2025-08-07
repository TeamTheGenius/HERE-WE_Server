package com.genius.herewe.core.security.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
	REFRESH_TOKEN("refresh_token:"),
	REGISTRATION_TOKEN("registration_token:"),
	AUTH_TOKEN("auth_token:");

	private final String PREFIX;
}
