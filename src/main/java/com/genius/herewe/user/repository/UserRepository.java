package com.genius.herewe.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.user.domain.ProviderInfo;
import com.genius.herewe.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select u from User u where u.email = :email and u.providerInfo = :providerInfo")
	Optional<User> findByOAuth2Info(@Param("email") String email, @Param("providerInfo") ProviderInfo providerInfo);

	@Query("select u from User u where u.nickname = :nickname")
	Optional<User> findByNickname(@Param("nickname") String nickname);
}
