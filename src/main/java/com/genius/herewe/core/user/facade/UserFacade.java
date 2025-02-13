package com.genius.herewe.core.user.facade;

import com.genius.herewe.core.user.dto.SignupRequest;
import com.genius.herewe.core.user.dto.SignupResponse;

public interface UserFacade {
	void isNicknameDuplicated(String nickname);

	SignupResponse signup(SignupRequest signupRequest);
}