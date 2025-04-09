package com.genius.herewe.core.user.facade;

import com.genius.herewe.core.security.dto.AuthResponse;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.dto.SignupRequest;
import com.genius.herewe.core.user.dto.SignupResponse;

public interface UserFacade {
	User findUser(Long userId);

	void validateNickname(String nickname);

	SignupResponse signup(SignupRequest signupRequest);

	AuthResponse getAuthInfo(Long userId);
}