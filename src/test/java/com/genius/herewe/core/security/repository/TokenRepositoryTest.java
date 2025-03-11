package com.genius.herewe.core.security.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.genius.herewe.core.security.domain.Token;
import com.genius.herewe.core.security.fixture.TokenFixture;

@SpringBootTest
@Testcontainers
public class TokenRepositoryTest {
	@Container
	static GenericContainer<?> redis = new GenericContainer<>("redis:7.0")
		.withExposedPorts(6379);
	@Autowired
	private TokenRepository tokenRepository;

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.redis.host", redis::getHost);
		registry.add("spring.redis.port", redis::getFirstMappedPort);
	}

	@Nested
	@DisplayName("저장되어 있는 Token 엔티티에 대해")
	class Context_saved_token {
		Token token = TokenFixture.createDefault();

		@BeforeEach
		void init() {
			tokenRepository.saveRefreshToken(token);
		}

		@Nested
		@DisplayName("userId를 통해 Token 조회를 시도할 때")
		class Describe_try_find_token {
			@Test
			@DisplayName("token이 존재하지 않는다면 Optional.empty()를 반환한다.")
			public void it_return_empty_entity() {
				Optional<Token> foundToken = tokenRepository.findRefreshToken(2L);
				assertThat(foundToken).isEmpty();
			}

			@Test
			@DisplayName("token이 존재한다면 Optional로 감싸서 반환한다.")
			public void it_return_present_entity() {
				Optional<Token> foundToken = tokenRepository.findRefreshToken(token.getUserId());

				//then
				assertThat(foundToken).isPresent();
				assertThat(foundToken.get().getUserId()).isEqualTo(token.getUserId());
				assertThat(foundToken.get().getNickname()).isEqualTo(token.getNickname());
				assertThat(foundToken.get().getToken()).isEqualTo(token.getToken());
			}
		}

		@Nested
		@DisplayName("delete() 메서드를 통해 삭제를 시도할 때")
		class Describe_try_delete_token {
			@Test
			@DisplayName("userId에 해당하는 Key가 있다면 삭제가 완료된다.")
			public void it_delete_successfully() {
				tokenRepository.deleteRefreshToken(token.getUserId());

				Optional<Token> foundToken = tokenRepository.findRefreshToken(token.getUserId());
				assertThat(foundToken).isEmpty();
			}
		}
	}
}