package com.genius.herewe.core.security.handler;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.global.exception.ErrorCode;
import com.genius.herewe.core.security.domain.UserPrincipal;
import com.genius.herewe.core.security.service.token.RegistrationTokenService;
import com.genius.herewe.core.user.domain.ProviderInfo;
import com.genius.herewe.core.user.domain.Role;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final String SIGNUP_URL;
	private final String AUTH_URL;
	private final UserRepository userRepository;
	private final RegistrationTokenService registrationTokenService;

	public OAuth2SuccessHandler(@Value("${url.base}") String BASE_URL,
								@Value("${url.path.signup}") String SIGN_UP_PATH,
								@Value("${url.path.auth}") String AUTH_PATH,
								UserRepository userRepository,
								RegistrationTokenService registrationTokenService) {
		this.userRepository = userRepository;
		this.registrationTokenService = registrationTokenService;
		this.SIGNUP_URL = BASE_URL + SIGN_UP_PATH;
		this.AUTH_URL = BASE_URL + AUTH_PATH;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {

		UserPrincipal oAuth2User = (UserPrincipal)authentication.getPrincipal();
		String email = oAuth2User.getName();
		ProviderInfo providerInfo = oAuth2User.getProviderInfo();

		User user = userRepository.findByOAuth2Info(email, providerInfo)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		String registrationToken = registrationTokenService.generateTokenForUser(user.getId());

		String redirectUrl = getRedirectUrlByRole(user.getRole(), user.getId(), registrationToken);
		getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}

	private String getRedirectUrlByRole(Role role, Long userId, String token) {
		if (role == Role.NOT_REGISTERED) {
			return UriComponentsBuilder.fromUri(URI.create(SIGNUP_URL))
				.queryParam("token", token)
				.build()
				.toUriString();
		}
		return UriComponentsBuilder.fromUri(URI.create(AUTH_URL))
			.queryParam("id", userId)
			.build()
			.toUriString();
	}
}
