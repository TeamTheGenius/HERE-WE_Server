package com.genius.herewe.business.moment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genius.herewe.business.moment.domain.Moment;

public interface MomentRepository extends JpaRepository<Moment, Long> {
}
