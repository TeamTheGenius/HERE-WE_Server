package com.genius.herewe.core.security.fixture;

import com.genius.herewe.core.security.domain.Token;

public class TokenFixture {
	public static Token createDefault() {
		return builder()
			.userId(1L)
			.nickname("nickname")
			.token("token")
			.ttl(3600)
			.build();
	}

	public static Token createWithUserId(Long userId) {
		return builder()
			.userId(userId)
			.build();
	}

	public static Token createWithToken(String token) {
		return builder()
			.token(token)
			.build();
	}

	public static TokenBuilder builder() {
		return new TokenBuilder();
	}

	public static class TokenBuilder {
		private Long userId = 1L;
		private String nickname = "nickname";
		private String token = "token";
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

		public TokenBuilder ttl(long ttl) {
			this.ttl = ttl;
			return this;
		}

		public Token build() {
			return Token.builder()
				.userId(userId)
				.nickname(nickname)
				.token(token)
				.ttl(ttl)
				.build();
		}
	}
}
