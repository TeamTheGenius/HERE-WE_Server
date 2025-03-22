package com.genius.herewe.business.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genius.herewe.business.location.domain.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
