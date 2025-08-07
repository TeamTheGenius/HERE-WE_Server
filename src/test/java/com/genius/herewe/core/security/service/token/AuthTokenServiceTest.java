package com.genius.herewe.core.security.service.token;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.domain.Token;
import com.genius.herewe.core.security.fixture.TokenFixture;
import com.genius.herewe.core.security.repository.TokenRepository;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {
	private final long TTL = 1800;
	@Mock
	private TokenRepository tokenRepository;
	private AuthTokenService authTokenService;

	@BeforeEach
	void init() {
		authTokenService = new AuthTokenService(tokenRepository, TTL);
	}

	@Nested
	@DisplayName("registration token 조회 시도 시")
	class Context_try_inquiry_registration_token {
		Token token = TokenFixture.createRegToken();

		@Nested
		@DisplayName("token 문자열을 전달했을 때")
		class Describe_pass_token_string {
			@Test
			@DisplayName("전달받은 문자열에 해당하는 token이 없다면 REGISTRATION_TOKEN_NOT_FOUND 예외가 발생한다.")
			public void it_throws_REGISTRATION_TOKEN_NOT_FOUND_exception() {
				//given
				given(tokenRepository.findRegistrationToken(token.getToken())).willReturn(Optional.empty());

				//when & then
				assertThatThrownBy(() -> authTokenService.getUserIdFromToken(token.getToken()))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(TOKEN_NOT_FOUND_IN_REDIS.getMessage());
			}

			@Test
			@DisplayName("일치하는 token이 있다면 해당 토큰의 userId를 반환한다.")
			public void it_returns_userId() {
				//given
				given(tokenRepository.findRegistrationToken(token.getToken())).willReturn(Optional.of(token));

				//when
				Long userId = authTokenService.getUserIdFromToken(token.getToken());

				//then
				assertThat(userId).isEqualTo(token.getUserId());
			}
		}
	}
}