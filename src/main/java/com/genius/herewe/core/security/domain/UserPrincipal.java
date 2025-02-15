package com.genius.herewe.core.security.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.genius.herewe.core.user.domain.ProviderInfo;
import com.genius.herewe.core.user.domain.User;

import lombok.Getter;

@Getter
public class UserPrincipal implements OAuth2User, UserDetails {
	private User user;
	private ProviderInfo providerInfo;
	private String nameAttributeKey;
	private Map<String, Object> attributes;
	private Collection<? extends GrantedAuthority> authorities;

	public UserPrincipal(User user) {
		this.user = user;
		this.authorities = Collections.singletonList(
			new SimpleGrantedAuthority(user.getRole().getKey()));
	}

	public UserPrincipal(User user, ProviderInfo providerInfo, Map<String, Object> attributes,
		String nameAttributeKey) {
		this.user = user;
		this.providerInfo = providerInfo;
		this.authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getKey()));
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
	}

	/**
	 * OAuth2User method implements
	 */
	@Override
	public String getName() {
		return user.getEmail();
	}

	/**
	 * UserDetails method implements
	 */
	@Override
	public String getUsername() {
		return String.valueOf(user.getId());
	}

	@Override
	public String getPassword() {
		return null;
	}
}
