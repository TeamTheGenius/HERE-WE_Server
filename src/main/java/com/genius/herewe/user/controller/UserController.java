package com.genius.herewe.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genius.herewe.user.dto.SignupRequest;
import com.genius.herewe.user.dto.SignupResponse;
import com.genius.herewe.user.facade.UserFacade;
import com.genius.herewe.util.response.CommonResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
	private final UserFacade userFacade;

	@GetMapping("/auth/check-nickname")
	public CommonResponse checkNicknameDuplicated(@RequestParam(value = "nickname") String nickname) {
		userFacade.isNicknameDuplicated(nickname);
		return CommonResponse.ok();
	}

	@PostMapping("/auth/signup")
	public CommonResponse signup(@RequestBody SignupRequest signupRequest) {
		SignupResponse signup = userFacade.signup(signupRequest);
		return CommonResponse.ok();
	}
}
