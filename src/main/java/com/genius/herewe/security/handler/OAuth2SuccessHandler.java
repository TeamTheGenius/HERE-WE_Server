package com.genius.herewe.security.handler;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.genius.herewe.security.domain.UserPrincipal;
import com.genius.herewe.user.domain.ProviderInfo;
import com.genius.herewe.user.domain.Role;
import com.genius.herewe.user.domain.User;
import com.genius.herewe.user.repository.UserRepository;
import com.genius.herewe.util.exception.BusinessException;
import com.genius.herewe.util.exception.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final String SIGNUP_URL;
	private final String AUTH_URL;
	private final UserRepository userRepository;

	public OAuth2SuccessHandler(@Value("${url.base}") String BASE_URL,
		@Value("${url.path.signup}") String SIGN_UP_PATH,
		@Value("${url.path.auth}") String AUTH_PATH,
		UserRepository userRepository) {
		this.userRepository = userRepository;
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

		String redirectUrl = getRedirectUrlByRole(user.getRole(), user.getId());
		getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}

	private String getRedirectUrlByRole(Role role, Long userId) {
		if (role == Role.NOT_REGISTERED) {
			return UriComponentsBuilder.fromUri(URI.create(SIGNUP_URL))
				.queryParam("id", userId)
				.build()
				.toUriString();
		}
		return UriComponentsBuilder.fromUri(URI.create(AUTH_URL))
			.queryParam("id", userId)
			.build()
			.toUriString();
	}
}
