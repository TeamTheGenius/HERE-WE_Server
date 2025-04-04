package com.genius.herewe.core.security.handler;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private final String REDIRECT_URL;
	private final String ERROR_PARAM_PREFIX = "error";

	public OAuth2FailureHandler(@Value("${url.base}") String REDIRECT_URL) {
		this.REDIRECT_URL = REDIRECT_URL;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {
		String redirectUrl = UriComponentsBuilder.fromUri(URI.create(REDIRECT_URL))
			.queryParam(ERROR_PARAM_PREFIX, exception.getLocalizedMessage())
			.build()
			.toUriString();

		getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}
}
