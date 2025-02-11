package com.genius.herewe.user.facade;

import com.genius.herewe.user.dto.SignupRequest;
import com.genius.herewe.user.dto.SignupResponse;

public interface UserFacade {
	void isNicknameDuplicated(String nickname);

	SignupResponse signup(SignupRequest signupRequest);
}