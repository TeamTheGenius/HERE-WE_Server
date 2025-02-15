package com.genius.herewe.core.security.repository;

import org.springframework.data.repository.CrudRepository;

import com.genius.herewe.core.security.domain.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
