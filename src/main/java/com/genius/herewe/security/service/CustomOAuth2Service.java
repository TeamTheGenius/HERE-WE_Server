package com.genius.herewe.security.service;

import com.genius.herewe.security.domain.UserPrincipal;
import com.genius.herewe.security.dto.OAuth2UserInfo;
import com.genius.herewe.security.dto.OAuth2UserInfoFactory;
import com.genius.herewe.user.domain.ProviderInfo;
import com.genius.herewe.user.domain.Role;
import com.genius.herewe.user.domain.User;
import com.genius.herewe.user.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        ClientRegistration clientRegistration = userRequest.getClientRegistration();

        ProviderInfo providerInfo = ProviderInfo.from(clientRegistration.getRegistrationId());
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.createUserInfo(providerInfo, attributes);
        String email = oAuth2UserInfo.getEmail();

        User user = getUser(email, providerInfo);

        String userNameAttributeName = clientRegistration.getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();

        return new UserPrincipal(user, attributes, userNameAttributeName);
    }

    private User getUser(String email, ProviderInfo providerInfo) {
        Optional<User> optionalUser = userRepository.findByOAuth2Info(email, providerInfo);

        if (optionalUser.isEmpty()) {
            User unregistered = User.builder()
                    .email(email)
                    .role(Role.NOT_REGISTERED)
                    .providerInfo(providerInfo)
                    .build();
            return userRepository.save(unregistered);
        }
        return optionalUser.get();
    }
}
