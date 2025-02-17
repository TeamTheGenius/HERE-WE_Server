package com.genius.herewe.core.security.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.genius.herewe.core.user.domain.Role;

import io.jsonwebtoken.security.Keys;

public class JwtUtil {

	public static Map<String, Object> createHeader() {
		Map<String, Object> header = new HashMap<>();
		header.put("typ", "JWT");
		header.put("alg", "HS256");
		return header;
	}

	public static Map<String, Object> createClaims(String nickname, Role role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("nickname", nickname);
		claims.put("role", role);
		return claims;
	}

	public static Key getSigningKey(String secretKey) {
		String encodedKey = encodeToBase64(secretKey);
		return Keys.hmacShaKeyFor(encodedKey.getBytes(StandardCharsets.UTF_8));
	}

	private static String encodeToBase64(String secretKey) {
		return Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

}
