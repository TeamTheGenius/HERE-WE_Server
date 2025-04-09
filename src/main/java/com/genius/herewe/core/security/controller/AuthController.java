package com.genius.herewe.core.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.security.dto.AuthRequest;
import com.genius.herewe.core.security.dto.AuthResponse;
import com.genius.herewe.core.security.service.JwtFacade;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.facade.UserFacade;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController implements AuthApi {
	public final UserFacade userFacade;
	public final JwtFacade jwtFacade;

	@PostMapping("/auth")
	public SingleResponse<AuthResponse> authorize(HttpServletResponse response,
												  @RequestBody AuthRequest authRequest) {

		User user = userFacade.findUser(authRequest.userId());

		jwtFacade.generateAccessToken(response, user);
		jwtFacade.generateRefreshToken(response, user);
		jwtFacade.setReissuedHeader(response);

		AuthResponse authResponse = userFacade.getAuthInfo(user.getId());
		return new SingleResponse<>(HttpStatus.OK, authResponse);
	}

	@GetMapping("/auth/health-check")
	public String healthCheck() {
		return "health check OK";
	}
}
