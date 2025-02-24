package com.genius.herewe.core.user.fixture;

import com.genius.herewe.core.user.domain.ProviderInfo;
import com.genius.herewe.core.user.domain.Role;
import com.genius.herewe.core.user.domain.User;

public class UserFixture {

	public static User createDefault() {
		return builder().role(Role.USER)
			.nickname("nickname")
			.providerInfo(ProviderInfo.NAVER)
			.email("test@naver.com")
			.build();
	}

	public static UserBuilder builder() {
		return new UserBuilder();
	}

	public static class UserBuilder {
		private String nickname = "nickname";
		private Role role = Role.USER;
		private ProviderInfo providerInfo = ProviderInfo.NAVER;
		private String email = "test@naver.com"; // regex로 검사하기

		public UserBuilder nickname(String nickname) {
			this.nickname = nickname;
			return this;
		}

		public UserBuilder role(Role role) {
			this.role = role;
			return this;
		}

		public UserBuilder providerInfo(ProviderInfo providerInfo) {
			this.providerInfo = providerInfo;
			return this;
		}

		public UserBuilder email(String email) {
			this.email = email;
			return this;
		}

		public User build() {
			return User.builder()
				.nickname(nickname)
				.role(role)
				.providerInfo(providerInfo)
				.email(email)
				.build();
		}
	}
}
