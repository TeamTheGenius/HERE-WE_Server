package com.genius.herewe.core.security.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static com.genius.herewe.core.security.constants.JwtRule.*;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.util.JwtUtil;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
@Transactional(readOnly = true)
public class DefaultJwtFacade implements JwtFacade {
	private final CustomUserDetailsService customUserDetailsService;
	private final UserService userService;

	private final String ISSUER;
	private final Key ACCESS_SECRET_KEY;
	private final Key REFRESH_SECRET_KEY;
	private final long ACCESS_EXPIRATION;
	private final long REFRESH_EXPIRATION;

	public DefaultJwtFacade(CustomUserDetailsService customUserDetailsService,
		UserService userService,
		@Value("${jwt.issuer}") String ISSUER,
		@Value("${jwt.secret.access}") String ACCESS_SECRET_KEY,
		@Value("${jwt.secret.refresh}") String REFRESH_SECRET_KEY,
		@Value("${jwt.expiration.access}") long ACCESS_EXPIRATION,
		@Value("${jwt.expiration.refresh}") long REFRESH_EXPIRATION) {
		this.customUserDetailsService = customUserDetailsService;
		this.userService = userService;
		this.ISSUER = ISSUER;
		this.ACCESS_SECRET_KEY = JwtUtil.getSigningKey(ACCESS_SECRET_KEY);
		this.REFRESH_SECRET_KEY = JwtUtil.getSigningKey(REFRESH_SECRET_KEY);
		this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
		this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
	}

	@Override
	public String generateAccessToken(HttpServletResponse response, User user) {
		Long now = System.currentTimeMillis();
		String token = Jwts.builder()
			.setHeader(JwtUtil.createHeader())
			.setClaims(JwtUtil.createClaims(user.getNickname(), user.getRole()))
			.setSubject(String.valueOf(user.getId()))
			.setIssuer(ISSUER)
			.setExpiration(new Date(now + ACCESS_EXPIRATION))
			.setId(UUID.randomUUID().toString())
			.signWith(ACCESS_SECRET_KEY, SignatureAlgorithm.HS256)
			.compact();
		String accessToken = ACCESS_PREFIX + token;

		response.setHeader(ACCESS_HEADER.getValue(), accessToken);

		return accessToken;
	}

	@Override
	public String generateRefreshToken(HttpServletResponse response, User user) {
		Long now = System.currentTimeMillis();
		String refreshToken = Jwts.builder()
			.setHeader(JwtUtil.createHeader())
			.claim("nickname", user.getNickname())
			.setSubject(String.valueOf(user.getId()))
			.setExpiration(new Date(now + REFRESH_EXPIRATION))
			.setId(UUID.randomUUID().toString())
			.signWith(REFRESH_SECRET_KEY, SignatureAlgorithm.HS256)
			.compact();

		ResponseCookie cookie = setTokenToCookie(REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
		response.addHeader(REFRESH_ISSUE.getValue(), cookie.toString());

		// redis/mongodb에 저장 필요

		return refreshToken;
	}

	private ResponseCookie setTokenToCookie(String tokenPrefix, String token, long maxAgeSeconds) {
		return ResponseCookie.from(tokenPrefix, token)
			.path("/")
			.maxAge(maxAgeSeconds)
			.httpOnly(true)
			.sameSite("Strict")
			.secure(true)
			.build();
	}

	@Override
	public String resolveAccessToken(HttpServletRequest request) {
		String bearerHeader = request.getHeader(ACCESS_HEADER.getValue());
		if (bearerHeader == null || bearerHeader.isBlank()) {
			throw new BusinessException(JWT_NOT_FOUND_IN_HEADER);
		}
		return bearerHeader.trim().substring(7);
	}

	@Override
	public String resolveRefreshToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			throw new BusinessException(JWT_NOT_FOUND_IN_COOKIE);
		}

		return Arrays.stream(cookies)
			.filter(cookie -> cookie.getName().equals(REFRESH_PREFIX.getValue()))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow(() -> new BusinessException(JWT_NOT_FOUND_IN_COOKIE));
	}

	@Override
	public void validateAccessToken() {

	}

	@Override
	public void validateRefreshToken() {

	}
}
