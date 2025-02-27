package com.genius.herewe.core.security.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static com.genius.herewe.core.security.constants.JwtRule.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.constants.JwtStatus;
import com.genius.herewe.core.user.domain.Role;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.fixture.UserFixture;
import com.genius.herewe.core.user.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtFacadeTest {
	private static final String TEST_ISSUER = "test-issuer";
	private static final String TEST_ACCESS_SECRET = "testsecretkeytestsecretkeytestsecretkey";
	private static final String TEST_REFRESH_SECRET = "refreshsecretrefreshsecretrefreshsecret";
	private static final long TEST_ACCESS_EXPIRATION = 3600000; // 1시간
	private static final long TEST_REFRESH_EXPIRATION = 1209600000;

	@Mock
	private CustomUserDetailsService customUserDetailsService;
	@Mock
	private UserService userService;
	@Mock
	private TokenService tokenService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	private JwtFacade jwtFacade;

	@BeforeEach
	void init() {
		jwtFacade = new DefaultJwtFacade(
			customUserDetailsService, userService, tokenService,
			TEST_ISSUER, TEST_ACCESS_SECRET, TEST_REFRESH_SECRET,
			TEST_ACCESS_EXPIRATION, TEST_REFRESH_EXPIRATION
		);
	}

	@Nested
	@DisplayName("access-token 생성 시")
	class Context_create_access_token {
		@Nested
		@DisplayName("사용자의 ROLE 확인 시")
		class Describe_check_user_role {
			@Test
			@DisplayName("NOT_REGISTERED라면 UNAUTHORIZED_ISSUE 예외가 발생해야 한다.")
			public void it_throws_UNAUTHORIZED_ISSUE_exception_when_NOT_REGISTERED() {
				//given
				User notRegistered = UserFixture.createByRole(Role.NOT_REGISTERED);

				//when & then
				assertThatThrownBy(() -> jwtFacade.verifyIssueCondition(notRegistered))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(UNAUTHORIZED_ISSUE.getMessage());
			}

			@ParameterizedTest
			@DisplayName("USER 또는 ADMIN인 경우 예외가 발생하지 않는다.")
			@EnumSource(names = {"USER", "ADMIN"})
			public void it_does_not_throw_exception(Role role) {
				//given
				User user = UserFixture.createByRole(role);

				//when & then
				assertThatNoException()
					.isThrownBy(() -> jwtFacade.verifyIssueCondition(user));
			}
		}

		@Nested
		@DisplayName("사용자 정보를 전달하면")
		class Describe_pass_user_info {
			@Test
			@DisplayName("올바른 형식의 JWT 토큰이 생성되고 응답 헤더에 설정된다")
			void it_generate_token_to_header() {
				// given
				User user = UserFixture.createDefault();

				// when
				String accessToken = jwtFacade.generateAccessToken(response, user);

				// then
				assertThat(accessToken).startsWith(ACCESS_PREFIX.getValue());
				verify(response).setHeader(eq(ACCESS_HEADER.getValue()), eq(accessToken));
				verify(response).setHeader(eq(ACCESS_REISSUED_HEADER.getValue()), eq("false"));
			}
		}
	}

	@Nested
	@DisplayName("refresh-token 생성 시")
	class Context_create_refresh_token {
		@Nested
		@DisplayName("사용자의 ROLE 확인 시")
		class Describe_check_user_role {
			@Test
			@DisplayName("NOT_REGISTERED라면 UNAUTHORIZED_ISSUE 예외가 발생해야 한다.")
			public void it_throws_UNAUTHORIZED_ISSUE_exception_when_NOT_REGISTERED() {
				//given
				User notRegistered = UserFixture.createByRole(Role.NOT_REGISTERED);

				//when & then
				assertThatThrownBy(() -> jwtFacade.verifyIssueCondition(notRegistered))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(UNAUTHORIZED_ISSUE.getMessage());
			}

			@ParameterizedTest
			@DisplayName("USER 또는 ADMIN인 경우 예외가 발생하지 않는다.")
			@EnumSource(names = {"USER", "ADMIN"})
			public void it_does_not_throw_exception(Role role) {
				//given
				User user = UserFixture.createByRole(role);

				//when & then
				assertThatNoException()
					.isThrownBy(() -> jwtFacade.verifyIssueCondition(user));
			}
		}
		
		@Nested
		@DisplayName("사용자 정보를 전달하면")
		class Describe_pass_user_info {
			@Test
			@DisplayName("올바른 형식의 JWT 토큰이 생성되고 쿠키에 설정된다.")
			public void it_generate_token_to_cookie() {
				//given
				User user = UserFixture.createDefault();
				ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);

				//when
				String refreshToken = jwtFacade.generateRefreshToken(response, user);

				//then
				verify(response).addHeader(eq(REFRESH_ISSUE.getValue()), headerValueCaptor.capture());
				String cookieHeader = headerValueCaptor.getValue();
				assertThat(cookieHeader)
					.contains("refresh=" + refreshToken)
					.contains("Path=/")
					.contains("HttpOnly")
					.contains("Secure")
					.contains("SameSite=STRICT");

				verify(tokenService).saveRefreshToken(
					eq(user.getId()),
					eq(user.getNickname()),
					eq(refreshToken)
				);
			}
		}
	}

	@Nested
	@DisplayName("Header에서 토큰 추출 시")
	class Context_resolve_token_from_header {
		private MockHttpServletRequest mockRequest;

		@BeforeEach
		void init() {
			mockRequest = new MockHttpServletRequest();
		}

		@Nested
		@DisplayName("access-token 추출 시도 시")
		class Describe_resolve_access_token {
			String accessToken = "test.token.value";
			String bearerToken = ACCESS_PREFIX.getValue() + accessToken;

			@Test
			@DisplayName("올바른 Authorization 헤더가 있으면 토큰을 추출할 수 있다.")
			public void it_resolves_token_from_valid_header() {
				//given
				mockRequest.addHeader(ACCESS_HEADER.getValue(), bearerToken);

				//when
				String resolvedAccessToken = jwtFacade.resolveAccessToken(mockRequest);

				//then
				assertThat(resolvedAccessToken).isEqualTo(accessToken);
			}

			@Test
			@DisplayName("Authorization 헤더가 빈 문자열이라면 예외가 발생한다.")
			public void it_throws_JWT_NOT_FOUND_IN_HEADER_exception() {
				//given
				mockRequest.addHeader(ACCESS_HEADER.getValue(), "   ");

				//when & then
				assertThatThrownBy(() -> jwtFacade.resolveAccessToken(mockRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(JWT_NOT_FOUND_IN_HEADER.getMessage());
			}

			@Test
			@DisplayName("Authorization 헤더가 없으면 예외가 발생한다.")
			public void it_throws_exception_when_header_not_exist() {
				//given

				//when & then
				assertThatThrownBy(() -> jwtFacade.resolveAccessToken(mockRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(JWT_NOT_FOUND_IN_HEADER.getMessage());
			}
		}

		@Nested
		@DisplayName("refresh-token 추출 시도 시")
		class Describe_resolve_refresh_token {
			String refreshToken = "test.refresh.token";

			@Test
			@DisplayName("유효한 쿠키가 있으면 토큰을 추출한다.")
			public void it_resolved_token_when_cookie_is_valid() {
				//given
				Cookie refreshCookie = new Cookie(REFRESH_PREFIX.getValue(), refreshToken);
				mockRequest.setCookies(refreshCookie);

				//when
				String resolvedRefreshToken = jwtFacade.resolveRefreshToken(mockRequest);

				//then
				assertThat(resolvedRefreshToken).isEqualTo(refreshToken);
			}

			@Test
			@DisplayName("refresh 쿠키가 없으면 JWT_NOT_FOUND_IN_COOKIE 예외가 발생한다.")
			public void it_throws_JWT_NOT_FOUND_IN_COOKIE_exception() {
				//given
				Cookie fakeCookie = new Cookie("other", "other values");
				mockRequest.setCookies(fakeCookie);

				//when & then
				assertThatThrownBy(() -> jwtFacade.resolveRefreshToken(mockRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(JWT_NOT_FOUND_IN_COOKIE.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("토큰 유효성 검사 시")
	class Context_check_token_validation {
		@Nested
		@DisplayName("access-token 유효성 검사 시")
		class Describe_verify_access_token {
			@Test
			@DisplayName("유효한 토큰이라면 VALID를 반환한다.")
			public void it_returns_VALID() {
				//given
				User user = UserFixture.createDefault();
				String accessToken = jwtFacade.generateAccessToken(response, user).substring(7);

				//when
				JwtStatus status = jwtFacade.verifyAccessToken(accessToken);

				//then
				assertThat(status).isEqualTo(JwtStatus.VALID);
			}

			@Test
			@DisplayName("만료된 토큰이라면 EXPIRED를 반환해야 한다.")
			public void it_return_EXPIRED() {
				//given
				JwtFacade expiredJwtFacade = new DefaultJwtFacade(
					customUserDetailsService, userService, tokenService,
					TEST_ISSUER, TEST_ACCESS_SECRET, TEST_REFRESH_SECRET,
					0L, TEST_REFRESH_EXPIRATION
				);
				User user = UserFixture.createDefault();
				String accessToken = expiredJwtFacade.generateAccessToken(response, user).substring(7);

				//when
				JwtStatus status = expiredJwtFacade.verifyAccessToken(accessToken);

				//then
				assertThat(status).isEqualTo(JwtStatus.EXPIRED);
			}

			@Test
			@DisplayName("유효하지 않은 토큰이라면 JWT_NOT_VALID 예외가 발생한다.")
			public void it_throws_JWT_NOT_VALID_exception() {
				//given
				String fakeAccessToken = "fake.access.token";

				//when & then
				assertThatThrownBy(() -> jwtFacade.verifyAccessToken(fakeAccessToken))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(JWT_NOT_VALID.getMessage());
			}
		}

		@Nested
		@DisplayName("refresh-token 유효성 검사 시")
		class Describe_verify_refresh_token {
			@Test
			@DisplayName("유효한 토큰이라면 예외를 발생시키지 않는다.")
			public void it_does_not_throw_exception() {
				//given
				User user = UserFixture.createDefault();
				String refreshToken = jwtFacade.generateRefreshToken(response, user);

				//when & then
				assertThatNoException()
					.isThrownBy(() -> jwtFacade.verifyRefreshToken(refreshToken));
			}

			@Test
			@DisplayName("만료된 토큰인 경우에도 JWT_NOT_VALID 예외를 발생시킨다.")
			public void it_throw_JWT_NOT_VALID_exception() {
				//given
				User user = UserFixture.createDefault();
				JwtFacade expiredJwtFacade = new DefaultJwtFacade(
					customUserDetailsService, userService, tokenService,
					TEST_ISSUER, TEST_ACCESS_SECRET, TEST_REFRESH_SECRET,
					TEST_ACCESS_EXPIRATION, 0L
				);
				String expiredToken = expiredJwtFacade.generateRefreshToken(response, user);

				//when & then
				assertThatThrownBy(() -> expiredJwtFacade.verifyRefreshToken(expiredToken))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(JWT_NOT_VALID.getMessage());
			}

			@Test
			@DisplayName("유효하지 않은 토큰이라면 JWT_NOT_VALID 예외가 발생한다.")
			public void it_throws_JWT_NOT_VALID_exception() {
				//given
				String expiredToken = "refresh.invalid.token";

				//when & then
				assertThatThrownBy(() -> jwtFacade.verifyRefreshToken(expiredToken))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(JWT_NOT_VALID.getMessage());
			}
		}
	}
}