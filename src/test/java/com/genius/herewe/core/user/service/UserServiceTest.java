package com.genius.herewe.core.user.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.fixture.UserFixture;
import com.genius.herewe.core.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Nested
	@DisplayName("사용자를 조회하고자할 때")
	class Context_try_inquiry_user {
		User user = UserFixture.createDefault();

		@Nested
		@DisplayName("사용자의 식별자를 전달했을 때")
		class Describe_pass_PK {
			Long userId = 1L;

			@Test
			@DisplayName("사용자가 존재한다면 User 엔티티를 반환한다.")
			public void it_return_user_entity() {
				//given
				given(userRepository.findById(userId)).willReturn(Optional.ofNullable(user));

				//when
				User foundUser = userService.findById(userId);

				//then
				assertThat(foundUser).isNotNull();
				assertThat(foundUser.getNickname()).isEqualTo(user.getNickname());
				assertThat(foundUser.getRole()).isEqualTo(user.getRole());
				assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
			}

			@Test
			@DisplayName("사용자가 존재하지 않는다면 MEMBER_NOT_FOUND 예외가 발생한다.")
			public void it_throw_MEMBER_NOT_FOUND_exception() {
				//given
				given(userRepository.findById(anyLong())).willReturn(Optional.empty());

				//when & then
				assertThatThrownBy(() -> userService.findById(userId))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
			}
		}

		@Nested
		@DisplayName("사용자의 닉네임을 전달했을 때")
		class Describe_pass_nickname {
			String nickname = "nickname";

			@Test
			@DisplayName("사용자가 존재한다면 User 엔티티를 반환한다.")
			public void it_return_user_entity() {
				//given
				given(userRepository.findByNickname(user.getNickname()))
					.willReturn(Optional.ofNullable(user));

				//when
				Optional<User> optionalUser = userService.findByNickname(user.getNickname());

				//then
				assertThat(optionalUser).isPresent();
				assertThat(optionalUser.get().getNickname()).isEqualTo(user.getNickname());
				assertThat(optionalUser.get().getRole()).isEqualTo(user.getRole());
				assertThat(optionalUser.get().getEmail()).isEqualTo(user.getEmail());
			}

			@Test
			@DisplayName("사용자가 존재하지 않는다면 Optional.empty()를 반환한다.")
			public void it_throw_MEMBER_NOT_FOUND_exception() {
				//given
				String fakeNickname = "fake nickname";
				given(userRepository.findByNickname(fakeNickname)).willReturn(Optional.empty());

				//when
				Optional<User> optionalUser = userService.findByNickname(fakeNickname);

				// then
				assertThat(optionalUser).isEmpty();
			}
		}
	}
}