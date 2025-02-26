package com.genius.herewe.core.security.service;

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
import org.springframework.beans.factory.annotation.Value;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.domain.Token;
import com.genius.herewe.core.security.fixture.TokenFixture;
import com.genius.herewe.core.security.repository.TokenRepository;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
	@Mock
	private TokenRepository tokenRepository;

	private TokenService tokenService;

	@Value("${jwt.expiration.refresh}")
	private long TTL;

	@BeforeEach
	void init() {
		tokenService = new TokenService(tokenRepository, TTL);
	}

	@Nested
	@DisplayName("saveRefreshToken 메서드는")
	class Context_saveRefreshToken_method {
		@Nested
		@DisplayName("userId, nickname, token을 받아서")
		class Describe_pass_parameter {
			Long userId = 1L;
			String nickname = "nickname";
			String token = "token";

			@Test
			@DisplayName("성공적으로 저장할 수 있다.")
			public void it_can_save_successfully() {
				tokenService.saveRefreshToken(userId, nickname, token);

				verify(tokenRepository, times(1)).save(argThat(savedToken ->
					savedToken.getUserId().equals(userId) &&
						savedToken.getNickname().equals(nickname) &&
						savedToken.getToken().equals(token)
				));
			}
		}
	}

	@Nested
	@DisplayName("token 조회 시도 시")
	class Context_token_already_saved {
		@Nested
		@DisplayName("userId를 전달했을 때")
		class Describe_pass_userId {
			@Test
			@DisplayName("userId가 존재한다면 Token을 반환한다.")
			public void it_returns_token() {
				// given
				Token token = TokenFixture.createDefault();
				given(tokenRepository.findById(token.getUserId()))
					.willReturn(Optional.of(token));

				// when
				Token foundToken = tokenService.findByUserId(token.getUserId());

				// then
				assertThat(foundToken).isNotNull();
				assertThat(foundToken.getUserId()).isEqualTo(token.getUserId());
			}

			@Test
			@DisplayName("존재하지 않는 userId로 조회하면 REFRESH_NOT_FOUND_IN_DB 예외를 발생한다.")
			public void it_throws_REFRESH_NOT_FOUND_IN_DB_exception() {
				// given
				Long nonExistId = 999L;
				given(tokenRepository.findById(nonExistId))
					.willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> tokenService.findByUserId(nonExistId))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(REFRESH_NOT_FOUND_IN_DB.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("토큰 정보 갱신 시")
	class Context_try_update_token {
		String newToken = "newToken";

		@Nested
		@DisplayName("userId를 통해 Token을 조회할 때")
		class Describe_inquiry_token_by_userId {
			@Test
			@DisplayName("존재하지 않는 userId인 경우 REFRESH_NOT_FOUND_IN_DB 예외가 발생한다.")
			public void it_throws_REFRESH_NOT_FOUND_IN_DB_exception() {
				// given
				Long nonExistId = 999L;
				given(tokenRepository.findById(nonExistId)).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> tokenService.updateRefreshToken(nonExistId, newToken))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(REFRESH_NOT_FOUND_IN_DB.getMessage());
			}

			@Test
			@DisplayName("존재하는 userId인 경우 token의 내용이 바뀐다.")
			public void it_changes_token() {
				// given
				Long userId = 1L;
				Token token = TokenFixture.createWithUserId(userId);

				given(tokenRepository.findById(userId)).willReturn(Optional.of(token));
				given(tokenRepository.save(any(Token.class)))
					.willAnswer(invocation -> invocation.getArgument(0));

				// when
				Token updatedToken = tokenService.updateRefreshToken(userId, newToken);

				// then
				assertThat(updatedToken.getUserId()).isEqualTo(userId);
				assertThat(updatedToken.getNickname()).isEqualTo(token.getNickname());
				assertThat(updatedToken.getTtl()).isEqualTo(TTL);
				assertThat(updatedToken.getToken()).isEqualTo(newToken);

				verify(tokenRepository).save(argThat(target ->
					target.getUserId().equals(userId) &&
						target.getToken().equals(newToken)
				));
			}
		}
	}

	@Nested
	@DisplayName("refresh token 탈취 여부 확인 시")
	class Context_check_hijacked {
		@Nested
		@DisplayName("userId와 검사 대상 토큰을 전달했을 때")
		class Describe_pass_userId {
			@Test
			@DisplayName("userId가 존재한다면 Token을 반환한다.")
			public void it_returns_token() {
				Token token = TokenFixture.createDefault();
				given(tokenRepository.findById(token.getUserId()))
					.willReturn(Optional.of(token));

				Token foundToken = tokenService.findByUserId(token.getUserId());

				assertThat(foundToken).isNotNull();
				assertThat(foundToken.getUserId()).isEqualTo(token.getUserId());
			}

			@Test
			@DisplayName("존재하지 않는 userId로 조회하면 REFRESH_NOT_FOUND_IN_DB 예외를 발생한다.")
			public void it_throws_REFRESH_NOT_FOUND_IN_DB_exception() {
				Long nonExistId = 999L;
				given(tokenRepository.findById(nonExistId))
					.willReturn(Optional.empty());

				assertThatThrownBy(() -> tokenService.findByUserId(nonExistId))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(REFRESH_NOT_FOUND_IN_DB.getMessage());
			}
		}

		@Nested
		@DisplayName("저장되어 있던 토큰과 비교했을 때")
		class Describe_compare_with_stored_token {
			String targetToken = "target token";

			@Test
			@DisplayName("기존에 저장되어 있던 토큰과 같다면 false를 반환한다.")
			public void it_return_false_when_same() {
				//given
				Long userId = 1L;
				Token token = TokenFixture.builder()
					.userId(userId)
					.token(targetToken)
					.build();
				given(tokenRepository.findById(userId)).willReturn(Optional.ofNullable(token));

				//when
				boolean isHijacked = tokenService.isRefreshHijacked(userId, targetToken);

				//then
				assertThat(isHijacked).isFalse();
			}

			@Test
			@DisplayName("기존에 저장되어 있던 토큰과 다르다면 true를 반환한다.")
			public void it_return_true_when_not_same() {
				//given
				Long userId = 1L;
				Token token = TokenFixture.createWithUserId(userId);
				given(tokenRepository.findById(userId)).willReturn(Optional.ofNullable(token));

				//when
				boolean isHijacked = tokenService.isRefreshHijacked(userId, targetToken);

				//then
				assertThat(isHijacked).isTrue();
			}
		}
	}
}