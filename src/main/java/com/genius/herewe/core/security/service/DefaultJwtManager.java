package com.genius.herewe.core.security.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static com.genius.herewe.core.security.constants.JwtRule.*;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.constants.JwtStatus;
import com.genius.herewe.core.security.service.token.RefreshTokenService;
import com.genius.herewe.core.security.util.JwtUtil;
import com.genius.herewe.core.user.domain.Role;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DefaultJwtManager implements JwtManager {
	private final CustomUserDetailsService customUserDetailsService;
	private final UserService userService;
	private final RefreshTokenService refreshTokenService;

	private final String ISSUER;
	private final Key ACCESS_SECRET_KEY;
	private final Key REFRESH_SECRET_KEY;
	private final long ACCESS_EXPIRATION;
	private final long REFRESH_EXPIRATION;

	public DefaultJwtManager(CustomUserDetailsService customUserDetailsService,
							 UserService userService, RefreshTokenService refreshTokenService,
							 @Value("${jwt.issuer}") String ISSUER,
							 @Value("${jwt.secret.access}") String ACCESS_SECRET_KEY,
							 @Value("${jwt.secret.refresh}") String REFRESH_SECRET_KEY,
							 @Value("${jwt.expiration.access}") long ACCESS_EXPIRATION,
							 @Value("${jwt.expiration.refresh}") long REFRESH_EXPIRATION) {
		this.customUserDetailsService = customUserDetailsService;
		this.userService = userService;
		this.refreshTokenService = refreshTokenService;
		this.ISSUER = ISSUER;
		this.ACCESS_SECRET_KEY = JwtUtil.getSigningKey(ACCESS_SECRET_KEY);
		this.REFRESH_SECRET_KEY = JwtUtil.getSigningKey(REFRESH_SECRET_KEY);
		this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
		this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
	}

	@Override
	public void verifyIssueCondition(User user) {
		if (user.getRole() == Role.NOT_REGISTERED) {
			throw new BusinessException(UNAUTHORIZED_ISSUE);
		}
	}

	@Override
	public String generateAccessToken(HttpServletResponse response, User user) {
		verifyIssueCondition(user);

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
		String accessToken = ACCESS_PREFIX.getValue() + token;

		response.setHeader(ACCESS_HEADER.getValue(), accessToken);
		response.setHeader(ACCESS_REISSUED_HEADER.getValue(), "false");

		return accessToken;
	}

	@Override
	public String generateRefreshToken(HttpServletResponse response, User user) {
		verifyIssueCondition(user);

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

		refreshTokenService.generateToken(user.getId(), user.getNickname(), refreshToken);

		return refreshToken;
	}

	private ResponseCookie setTokenToCookie(String tokenPrefix, String token, long maxAgeSeconds) {
		return ResponseCookie.from(tokenPrefix, token)
			.path("/")
			.maxAge(maxAgeSeconds)
			.httpOnly(true)
			.sameSite(org.springframework.boot.web.server.Cookie.SameSite.STRICT.name())
			.secure(true)
			.build();
	}

	@Override
	public void setReissuedHeader(HttpServletResponse response) {
		response.setHeader(ACCESS_REISSUED_HEADER.getValue(), "true");
	}

	@Override
	public String resolveAccessToken(HttpServletRequest request) {
		String bearerHeader = request.getHeader(ACCESS_HEADER.getValue());
		if (bearerHeader == null || bearerHeader.isEmpty()) {
			return "";
		} else if (!bearerHeader.startsWith(ACCESS_PREFIX.getValue())) {
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
	public JwtStatus verifyAccessToken(String accessToken) {
		if (accessToken.isEmpty()) {
			return JwtStatus.NEED_CHECK_RT;
		}
		try {
			Jwts.parserBuilder()
				.setSigningKey(ACCESS_SECRET_KEY)
				.build()
				.parseClaimsJws(accessToken);
			return JwtStatus.VALID;
		} catch (ExpiredJwtException e) {
			return JwtStatus.EXPIRED;
		} catch (JwtException e) {
			throw new BusinessException(JWT_NOT_VALID);
		}
	}

	@Override
	public void verifyRefreshToken(String refreshToken) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(REFRESH_SECRET_KEY)
				.build()
				.parseClaimsJws(refreshToken);
		} catch (JwtException e) {
			throw new BusinessException(JWT_NOT_VALID);
		}
	}

	@Override
	public boolean isRefreshHijacked(Long userId, String refreshToken) {
		return refreshTokenService.isRefreshHijacked(userId, refreshToken);
	}

	@Override
	public User getPKFromRefresh(String refreshToken) {
		String pk = Jwts.parserBuilder()
			.setSigningKey(REFRESH_SECRET_KEY)
			.build()
			.parseClaimsJws(refreshToken)
			.getBody()
			.getSubject();
		Long userId = Long.valueOf(pk);
		return userService.findById(userId);
	}

	@Override
	public Authentication createAuthentication(String accessToken) {
		if (accessToken.startsWith(ACCESS_PREFIX.getValue())) {
			accessToken = accessToken.trim().substring(7);
		}
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(extractUserPK(accessToken));
		return new UsernamePasswordAuthenticationToken(
			userDetails, "", userDetails.getAuthorities()
		);
	}

	@Override
	public void logout(HttpServletResponse response, Long userId) {
		refreshTokenService.delete(userId);

		Cookie cookie = new Cookie(REFRESH_PREFIX.getValue(), null);
		cookie.setMaxAge(0);
		cookie.setPath("/");

		response.addCookie(cookie);
	}

	private String extractUserPK(String accessToken) {
		return Jwts.parserBuilder()
			.setSigningKey(ACCESS_SECRET_KEY)
			.build()
			.parseClaimsJws(accessToken)
			.getBody()
			.getSubject();
	}
}
