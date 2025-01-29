package com.genius.herewe.security.domain;

import com.genius.herewe.user.domain.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class UserPrincipal implements OAuth2User {
    private User user;
    private String nameAttributeKey;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.user = user;
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getKey()));
    }

    public UserPrincipal(User user, Map<String, Object> attributes, String nameAttributeKey) {
        this.user = user;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getKey()));
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }
}
