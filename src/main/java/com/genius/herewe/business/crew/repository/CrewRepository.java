package com.genius.herewe.business.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genius.herewe.business.crew.domain.Crew;

public interface CrewRepository extends JpaRepository<Crew, Long> {
}
