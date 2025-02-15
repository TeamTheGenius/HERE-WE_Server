package com.genius.herewe.core.security.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash
public class RefreshToken {
	@Id
	private Long userId;

	private String nickname;

	@Indexed
	private String token;

	@TimeToLive
	private long ttl;

	@Builder
	public RefreshToken(Long userId, String nickname, String token, long ttl) {
		this.userId = userId;
		this.nickname = nickname;
		this.token = token;
		this.ttl = ttl;
	}
}
