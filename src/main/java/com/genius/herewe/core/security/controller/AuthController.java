package com.genius.herewe.core.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.security.dto.AuthRequest;
import com.genius.herewe.core.security.dto.AuthResponse;
import com.genius.herewe.core.security.facade.AuthFacade;
import com.genius.herewe.core.user.facade.UserFacade;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController implements AuthApi {
	public final UserFacade userFacade;
	private final AuthFacade authFacade;

	@PostMapping("/auth")
	public SingleResponse<AuthResponse> authorize(HttpServletResponse response,
												  @RequestBody AuthRequest authRequest) {
		Long userId = authFacade.authorize(response, authRequest);
		AuthResponse authResponse = userFacade.getAuthInfo(userId);
		return new SingleResponse<>(HttpStatus.OK, authResponse);
	}

	@PostMapping("/auth/reissue")
	public SingleResponse<AuthResponse> reissueToken(HttpServletRequest request, HttpServletResponse response) {
		Long userId = authFacade.reissueToken(request, response);
		AuthResponse authResponse = userFacade.getAuthInfo(userId);
		return new SingleResponse<>(HttpStatus.OK, authResponse);
	}

	@PostMapping("/auth/logout")
	public CommonResponse logout(HttpServletResponse response, @RequestParam String nickname) {
		authFacade.logout(response, nickname);
		return CommonResponse.ok();
	}

	@GetMapping("/auth/health-check")
	public String healthCheck() {
		return "health check OK";
	}
}
