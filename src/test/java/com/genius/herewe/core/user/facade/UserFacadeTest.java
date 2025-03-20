package com.genius.herewe.core.user.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Objects;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.domain.Token;
import com.genius.herewe.core.security.dto.AuthResponse;
import com.genius.herewe.core.security.fixture.TokenFixture;
import com.genius.herewe.core.security.service.token.RegistrationTokenService;
import com.genius.herewe.core.user.domain.Role;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.dto.SignupRequest;
import com.genius.herewe.core.user.dto.SignupResponse;
import com.genius.herewe.core.user.fixture.UserFixture;
import com.genius.herewe.core.user.service.UserService;
import com.genius.herewe.infra.file.service.FilesManager;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {
	@Mock
	private FilesManager filesManager;
	@Mock
	private UserService userService;
	@Mock
	private RegistrationTokenService registrationTokenService;
	private UserFacade userFacade;

	@BeforeEach
	public void init() {
		userFacade = new DefaultUserFacade(userService, registrationTokenService, filesManager);
	}

	@Nested
	@DisplayName("사용자 조회 시")
	class Context_inquiry_user {
		private Long userId = 1L;
		private User user;

		@BeforeEach
		void init() {
			user = UserFixture.createById(userId);
		}

		@Nested
		@DisplayName("userId를 전달했을 때")
		class Describe_pass_userId {
			@BeforeEach
			void init() {
				given(userService.findById(anyLong()))
					.willAnswer(invocation -> {
						Long id = invocation.getArgument(0);
						if (id.equals(userId)) {
							return user;
						}
						throw new BusinessException(MEMBER_NOT_FOUND);
					});
			}

			@Test
			@DisplayName("회원이 존재한다면 User를 반환한다")
			public void it_return_user() {
				//when
				User foundUser = userFacade.findUser(userId);

				//then
				assertThat(foundUser.getId()).isEqualTo(userId);
				assertThat(foundUser.getNickname()).isEqualTo(user.getNickname());
				assertThat(foundUser.getRole()).isEqualTo(user.getRole());
				assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
			}

			@Test
			@DisplayName("존재하지 않는 id라면 MEMBER_NOT_FOUND 예외가 발생한다.")
			public void it_throws_MEMBER_NOT_FOUND_exception() {
				//given
				Long fakeId = 999L;

				//when&then
				Assertions.assertThatThrownBy(() -> userFacade.findUser(fakeId))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("닉네임 중복 확인 시")
	class Context_check_nickname_duplicated {
		@Nested
		@DisplayName("닉네임을 전달했을 때")
		class Describe_pass_nickname {
			String existNickname = "nickname";
			User user = UserFixture.createByNickname(existNickname);

			@Test
			@DisplayName("기존 회원들과 닉네임이 겹치치 않으면 예외를 발생시키지 않는다.")
			public void it_does_not_throw_NICKNAME_DUPLICATED_exception() {
				//given
				String targetNickname = "otherNickname";
				given(userService.findByNickname(anyString()))
					.willAnswer(invocation -> {
						String target = invocation.getArgument(0);
						if (target.equals(existNickname))
							return Optional.ofNullable(user);
						return Optional.empty();
					});

				//when & then
				assertThatNoException()
					.isThrownBy(() -> userFacade.validateNickname(targetNickname));
			}

			@Test
			@DisplayName("기존 회원들과 한 명이라도 닉네임이 겹치면 NICKNAME_DUPLICATED 예외를 발생시킨다.")
			public void it_throw_NICKNAME_DUPLICATED_exception() {
				//given
				given(userService.findByNickname(anyString()))
					.willAnswer(invocation -> {
						String target = invocation.getArgument(0);
						if (target.equals(existNickname))
							return Optional.ofNullable(user);
						return Optional.empty();
					});

				//when & then
				assertThatThrownBy(() -> userFacade.validateNickname(existNickname))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(NICKNAME_DUPLICATED.getMessage());
			}

			@ParameterizedTest
			@DisplayName("닉네임이 null이거나 빈 문자열인 경우 INVALID_NICKNAME 예외를 발생시킨다.")
			@NullAndEmptySource
			public void it_throw_INVALID_NICKNAME_exception_when_null_or_empty(String nickname) {
				assertThatThrownBy(() -> userFacade.validateNickname(nickname))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(INVALID_NICKNAME.getMessage());
			}

			@ParameterizedTest
			@DisplayName("닉네임이 2~20자 사이가 아니거나, 한글&영문자&숫자 외에 다른 문자로 이루어져있다면 INVALID_NICKNAME 예외를 발생시킨다.")
			@ValueSource(strings = {"a", "abcdefghijklmnopsdfds", "도토리@"})
			public void it_throw_INVALID_NICKNAME_exception_when_length_invalid(String nickname) {
				assertThatThrownBy(() -> userFacade.validateNickname(nickname))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(INVALID_NICKNAME.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("회원가입 요청 시")
	class Context_request_signup {
		Token token = TokenFixture.createRegToken();
		String registrationToken = token.getToken();

		@Nested
		@DisplayName("사용자의 닉네임 중복을 확인할 때")
		class Describe_check_nickname_duplicated {
			String existNickname = "nickname";
			User user = UserFixture.builder()
				.nickname(existNickname)
				.role(Role.NOT_REGISTERED)
				.build();

			@BeforeEach
			void init() {
				given(userService.findById(user.getId())).willReturn(user);
				given(userService.findByNickname(anyString()))
					.willAnswer(invocation -> {
						String target = invocation.getArgument(0);
						if (target.equals(existNickname))
							return Optional.ofNullable(user);
						return Optional.empty();
					});
			}

			@Test
			@DisplayName("기존 회원들과 닉네임이 겹치치 않으면 예외를 발생시키지 않는다.")
			public void it_does_not_throw_NICKNAME_DUPLICATED_exception() {
				//given
				String targetNickname = "otherNickname";
				SignupRequest signupRequest = new SignupRequest(
					registrationToken, targetNickname
				);

				given(registrationTokenService.getUserIdFromToken(registrationToken)).willReturn(user.getId());

				//when & then
				assertThatNoException()
					.isThrownBy(() -> userFacade.signup(signupRequest));
			}

			@Test
			@DisplayName("기존 회원들과 한 명이라도 닉네임이 겹치면 NICKNAME_DUPLICATED 예외를 발생시킨다.")
			public void it_throw_NICKNAME_DUPLICATED_exception() {
				// given
				SignupRequest signupRequest = new SignupRequest(
					registrationToken, existNickname
				);
				given(registrationTokenService.getUserIdFromToken(registrationToken)).willReturn(user.getId());

				//when & then
				assertThatThrownBy(() -> userFacade.signup(signupRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(NICKNAME_DUPLICATED.getMessage());
			}
		}

		@Nested
		@DisplayName("사용자의 Role을 검사할 때")
		class Describe_check_user_role {
			@Test
			@DisplayName("사용자의 Role이 NOT_REGISTERED인 경우 회원가입이 정상적으로 진행된다.")
			public void it_signup_successfully() {
				//given
				User user = UserFixture.createByRole(Role.NOT_REGISTERED);
				SignupRequest signupRequest = new SignupRequest(registrationToken, user.getNickname());

				given(userService.findById(user.getId())).willReturn(user);
				given(registrationTokenService.getUserIdFromToken(registrationToken)).willReturn(user.getId());

				//when
				SignupResponse signupResponse = userFacade.signup(signupRequest);

				//then
				assertThat(signupResponse.userId()).isEqualTo(user.getId());
				assertThat(signupResponse.nickname()).isEqualTo(user.getNickname());
			}

			@ParameterizedTest
			@EnumSource(names = {"USER", "ADMIN"})
			@DisplayName("사용자의 Role이 NOT_REGISTERED가 아닌 경우 ALREADY_REGISTERED 예외가 발생한다.")
			public void it_throws_ALREADY_REGISTERED_exception(Role role) {
				//given
				User user = UserFixture.createByRole(role);
				SignupRequest signupRequest = new SignupRequest(registrationToken, user.getNickname());

				given(userService.findById(user.getId())).willReturn(user);
				given(registrationTokenService.getUserIdFromToken(registrationToken)).willReturn(user.getId());

				//when & then
				assertThatThrownBy(() -> userFacade.signup(signupRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(ALREADY_REGISTERED.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("사용자의 정보 조회 시")
	class Context_inquiry_user_info {
		@Nested
		@DisplayName("사용자의 id를 전달했을 때")
		class Describe_pass_userId {
			User user = UserFixture.createDefault();

			@BeforeEach
			void init() {
				given(userService.findById(anyLong()))
					.willAnswer(invocation -> {
						Long passedId = invocation.getArgument(0);
						if (Objects.equals(passedId, user.getId()))
							return user;
						throw new BusinessException(MEMBER_NOT_FOUND);
					});
			}

			@Test
			@DisplayName("사용자의 id가 존재한다면 사용자의 정보를 전달한다.")
			public void it_pass_user_info() {
				//given

				//when
				AuthResponse authResponse = userFacade.getAuthInfo(user.getId());

				//then
				assertThat(authResponse.userId()).isEqualTo(user.getId());
				assertThat(authResponse.nickname()).isEqualTo(user.getNickname());
			}

			@Test
			@DisplayName("사용자 id가 존재하지 않는다면 MEMBER_NOT_FOUND 예외가 발생한다.")
			public void it_throws_MEMBER_NOT_FOUND_exception() {
				//given
				Long nonExistId = 999L;

				//when & then
				assertThatThrownBy(() -> userFacade.getAuthInfo(nonExistId))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
			}
		}
	}
}