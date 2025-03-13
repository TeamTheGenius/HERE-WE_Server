package com.genius.herewe.business.invitation.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.invitation.domain.Invitation;
import com.genius.herewe.business.invitation.repository.InvitationRepository;
import com.genius.herewe.core.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvitationService {
	private final InvitationRepository invitationRepository;

	@Transactional
	public Invitation save(Invitation invitation) {
		return invitationRepository.save(invitation);
	}

	public Optional<Invitation> findOptional(Long userId, Long crewId) {
		return invitationRepository.findOptional(userId, crewId);
	}

	public Invitation findByToken(String token) {
		return invitationRepository.findOptionalByToken(token)
			.orElseThrow(() -> new BusinessException(INVITATION_NOT_FOUND));
	}

	@Transactional
	public void delete(Invitation invitation) {
		invitationRepository.delete(invitation);
	}
}
