package com.genius.herewe.business.invitation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genius.herewe.business.invitation.domain.Invitation;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

	@Query("SELECT i FROM Invitation i WHERE i.user.id = :userId AND i.crew.id = :crewId")
	Optional<Invitation> findOptional(@Param("userId") Long userId, @Param("crewId") Long crewId);

	@Query("SELECT i FROM Invitation i WHERE i.token = :token")
	Optional<Invitation> findOptionalByToken(@Param("token") String token);
}
