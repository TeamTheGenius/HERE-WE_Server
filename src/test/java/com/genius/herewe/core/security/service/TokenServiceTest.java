package com.genius.herewe.core.security.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.genius.herewe.core.security.domain.Token;

@SpringBootTest
class TokenServiceTest {
	@Autowired
	private TokenService tokenService;

	@Test
	void saveAndFindRefreshTokenTest() {
		// given
		Long userId = 1L;
		String nickname = "testUser";
		String token = "testToken";

		// when
		tokenService.saveRefreshToken(userId, nickname, token);
		Token found = tokenService.findByUserId(userId);

		// then
		assertThat(found.getUserId()).isEqualTo(userId);
		assertThat(found.getNickname()).isEqualTo(nickname);
	}
}