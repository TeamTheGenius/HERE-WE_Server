package com.genius.herewe.business.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genius.herewe.business.crew.domain.CrewMember;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
}
