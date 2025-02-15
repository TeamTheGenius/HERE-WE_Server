package com.genius.herewe.core.security.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.genius.herewe.core.security.domain.RefreshToken;

@SpringBootTest
class RefreshTokenServiceTest {
	@Autowired
	private RefreshTokenService refreshTokenService;

	@Test
	void saveAndFindRefreshTokenTest() {
		// given
		Long userId = 1L;
		String nickname = "testUser";
		String token = "testToken";

		// when
		refreshTokenService.saveRefreshToken(userId, nickname, token);
		RefreshToken found = refreshTokenService.findByUserId(userId);

		// then
		assertThat(found.getUserId()).isEqualTo(userId);
		assertThat(found.getNickname()).isEqualTo(nickname);
	}
}