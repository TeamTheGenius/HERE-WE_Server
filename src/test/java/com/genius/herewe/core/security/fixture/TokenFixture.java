package com.genius.herewe.core.security.fixture;

import com.genius.herewe.core.security.domain.Token;
import com.genius.herewe.core.security.domain.TokenType;

public class TokenFixture {
	public static Token createRefreshToken() {
		return builder()
			.userId(1L)
			.nickname("nickname")
			.token("token")
			.tokenType(TokenType.REFRESH_TOKEN)
			.ttl(3600)
			.build();
	}

	public static Token createRefreshWithUserId(Long userId) {
		return builder()
			.userId(userId)
			.build();
	}

	public static Token createRegToken() {
		return builder()
			.userId(1L)
			.nickname("nickname")
			.token("registration token")
			.tokenType(TokenType.REGISTRATION_TOKEN)
			.ttl(1800)
			.build();
	}

	public static TokenBuilder builder() {
		return new TokenBuilder();
	}

	public static class TokenBuilder {
		private Long userId = 1L;
		private String nickname = "nickname";
		private String token = "token";
		private TokenType tokenType = TokenType.REFRESH_TOKEN;
		private long ttl = 3600;

		public TokenBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public TokenBuilder nickname(String nickname) {
			this.nickname = nickname;
			return this;
		}

		public TokenBuilder token(String token) {
			this.token = token;
			return this;
		}

		public TokenBuilder tokenType(TokenType tokenType) {
			this.tokenType = tokenType;
			return this;
		}

		public TokenBuilder ttl(long ttl) {
			this.ttl = ttl;
			return this;
		}

		public Token build() {
			return Token.builder()
				.userId(userId)
				.nickname(nickname)
				.token(token)
				.tokenType(tokenType)
				.ttl(ttl)
				.build();
		}
	}
}
