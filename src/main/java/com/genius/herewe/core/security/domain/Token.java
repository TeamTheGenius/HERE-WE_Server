package com.genius.herewe.core.security.domain;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {
	@Id
	private Long userId;

	private String nickname;

	@Indexed
	private String token;

	private TokenType tokenType;

	@TimeToLive
	private long ttl;

	@Builder
	public Token(Long userId, String nickname, String token, TokenType tokenType, long ttl) {
		this.userId = userId;
		this.nickname = nickname;
		this.token = token;
		this.tokenType = tokenType;
		this.ttl = ttl;
	}

	public static Token create(Map<Object, Object> entries) {
		TokenType type = TokenType.valueOf((String)entries.getOrDefault("tokenType", TokenType.REFRESH_TOKEN.name()));

		return Token.builder()
			.userId(Long.valueOf((String)entries.get("userId")))
			.nickname((String)entries.getOrDefault("nickname", null))
			.token((String)entries.get("token"))
			.tokenType(type)
			.build();
	}

	/**
	 * 리프레시 토큰 생성
	 */
	public static Token createRefreshToken(Long userId, String nickname, String token, long ttl) {
		return Token.builder()
			.userId(userId)
			.nickname(nickname)
			.token(token)
			.tokenType(TokenType.REFRESH_TOKEN)
			.ttl(ttl)
			.build();
	}

	/**
	 * 회원가입 토큰 생성
	 */
	public static Token createRegistrationToken(Long userId, String token, long ttl) {
		return Token.builder()
			.userId(userId)
			.token(token)
			.tokenType(TokenType.REGISTRATION_TOKEN)
			.ttl(ttl)
			.build();
	}
}
