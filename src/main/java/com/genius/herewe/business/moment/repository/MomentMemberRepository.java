package com.genius.herewe.business.moment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genius.herewe.business.moment.domain.MomentMember;

public interface MomentMemberRepository extends JpaRepository<MomentMember, Long> {
}
