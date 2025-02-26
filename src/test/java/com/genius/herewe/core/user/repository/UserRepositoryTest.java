package com.genius.herewe.core.user.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.genius.herewe.core.user.domain.ProviderInfo;
import com.genius.herewe.core.user.domain.Role;
import com.genius.herewe.core.user.domain.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	private User testUser;
	private ProviderInfo providerInfo;

	@BeforeEach
	void setUp() {
		providerInfo = ProviderInfo.NAVER;

		testUser = User.builder()
			.email("test@example.com")
			.nickname("testUser")
			.providerInfo(providerInfo)
			.role(Role.USER)
			.build();

		userRepository.save(testUser);
	}

	@AfterEach
	void cleanup() {
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("OAuth2 정보로 사용자를 찾을 수 있다")
	void findByOAuth2Info_ShouldReturnUser() {
		// when
		Optional<User> found = userRepository.findByOAuth2Info(
			testUser.getEmail(),
			testUser.getProviderInfo()
		);

		// then
		assertThat(found).isPresent();
		assertThat(found.get().getEmail()).isEqualTo(testUser.getEmail());
		assertThat(found.get().getProviderInfo()).isEqualTo(testUser.getProviderInfo());
	}

	@Test
	@DisplayName("존재하지 않는 OAuth2 정보로 조회하면 빈 Optional을 반환한다")
	void findByOAuth2Info_WithNonExistentInfo_ShouldReturnEmpty() {
		// given
		ProviderInfo nonExistentProvider = ProviderInfo.NAVER;

		// when
		Optional<User> found = userRepository.findByOAuth2Info(
			"nonexistent@example.com",
			nonExistentProvider
		);

		// then
		assertThat(found).isEmpty();
	}

	@Test
	@DisplayName("닉네임으로 사용자를 찾을 수 있다")
	void findByNickname_ShouldReturnUser() {
		// when
		Optional<User> found = userRepository.findByNickname(testUser.getNickname());

		// then
		assertThat(found).isPresent();
		assertThat(found.get().getNickname()).isEqualTo(testUser.getNickname());
		assertThat(found.get().getEmail()).isEqualTo(testUser.getEmail());
	}

	@Test
	@DisplayName("존재하지 않는 닉네임으로 조회하면 빈 Optional을 반환한다")
	void findByNickname_WithNonExistentNickname_ShouldReturnEmpty() {
		// when
		Optional<User> found = userRepository.findByNickname("nonexistent");

		// then
		assertThat(found).isEmpty();
	}

	@Test
	@DisplayName("사용자 정보를 수정할 수 있다")
	void update_UserInformation_ShouldSucceed() {
		// given
		String updatedNickname = "updatedUser";
		testUser.updateNickname(updatedNickname);

		// when
		User updatedUser = userRepository.save(testUser);

		// then
		assertThat(updatedUser.getNickname()).isEqualTo(updatedNickname);
	}
}