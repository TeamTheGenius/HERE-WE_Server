package com.genius.herewe.business.invitation.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.domain.CrewRole;
import com.genius.herewe.business.crew.dto.CrewIdResponse;
import com.genius.herewe.business.crew.service.CrewMemberService;
import com.genius.herewe.business.crew.service.CrewService;
import com.genius.herewe.business.invitation.domain.Invitation;
import com.genius.herewe.business.invitation.dto.InvitationInfo;
import com.genius.herewe.business.invitation.dto.InvitationRequest;
import com.genius.herewe.business.invitation.service.InvitationService;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.service.UserService;
import com.genius.herewe.infra.mail.dto.MailRequest;
import com.genius.herewe.infra.mail.service.MailManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DefaultInvitationFacade implements InvitationFacade {
	private final int MAX_PARTICIPANT = 500;
	private final String BASE_URL;
	private final String INVITE_URL;
	private final UserService userService;
	private final CrewService crewService;
	private final CrewMemberService crewMemberService;
	private final InvitationService invitationService;
	private final MailManager mailManager;

	public DefaultInvitationFacade(@Value("${url.base}") String BASE_URL,
								   @Value("${url.path.invite}") String INVITE_URL,
								   UserService userService, CrewService crewService,
								   CrewMemberService crewMemberService,
								   InvitationService invitationService, MailManager mailManager) {
		this.BASE_URL = BASE_URL;
		this.INVITE_URL = INVITE_URL;
		this.userService = userService;
		this.crewService = crewService;
		this.crewMemberService = crewMemberService;
		this.invitationService = invitationService;
		this.mailManager = mailManager;
	}

	@Override
	@Transactional
	public void inviteCrew(InvitationRequest invitationRequest) {
		String nickname = invitationRequest.nickname();
		User user = userService.findByNickname(nickname)
			.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
		Crew crew = crewService.findById(invitationRequest.crewId());

		if (crew.getParticipantCount() >= MAX_PARTICIPANT) {
			throw new BusinessException(INVALID_CREW_CAPACITY);
		}
		if (crewMemberService.findOptional(user.getId(), crew.getId()).isPresent()) {
			throw new BusinessException(ALREADY_JOINED_CREW);
		}

		InvitationInfo invitationInfo = InvitationInfo.create(2);
		Invitation invitation = invitationService.findOptional(user.getId(), crew.getId())
			.map(existingInvitation -> {
				existingInvitation.update(
					invitationInfo.token(), invitationInfo.invitedAt(), invitationInfo.expiredAt()
				);
				return existingInvitation;
			})
			.orElseGet(() -> {
				Invitation newInvitation = Invitation.create(
					invitationInfo.token(), invitationInfo.invitedAt(), invitationInfo.expiredAt());
				newInvitation.inviteUser(user, crew);
				return invitationService.save(newInvitation);
			});

		MailRequest mailRequest = MailRequest.builder()
			.receiverMail(user.getEmail())
			.nickname(nickname)
			.crewName(crew.getName())
			.introduce(crew.getIntroduce())
			.memberCount(crew.getParticipantCount())
			.inviteUrl(BASE_URL + INVITE_URL + invitation.getToken())
			.build();

		mailManager.sendAsync(mailRequest).thenAccept(result -> {
			if (!result) {
				log.warn("Email sending failed for invitation: {}", invitation.getToken());
				invitationService.deleteInNewTransaction(invitation);
			} else {
				log.info("Email sent successfully for invitation: {}", invitation.getToken());
			}
		});
	}

	@Transactional
	public CrewIdResponse joinCrew(String inviteToken) {
		Invitation invitation = invitationService.findByToken(inviteToken);
		if (invitation.getExpiredAt().isBefore(LocalDateTime.now())) {
			invitationService.deleteInNewTransaction(invitation);
			throw new BusinessException(INVITATION_EXPIRED);
		}
		//NOTE: N+1 problem 확인해보기
		User user = userService.findById(invitation.getUser().getId());
		Crew crew = crewService.findById(invitation.getCrew().getId());

		Optional<CrewMember> optionalCrewMember = crewMemberService.findOptional(user.getId(), crew.getId());
		if (optionalCrewMember.isPresent()) {
			throw new BusinessException(ALREADY_JOINED_CREW);

		}

		crew.updateParticipantCount(1);
		CrewMember crewMember = CrewMember.createByRole(CrewRole.MEMBER);
		crewMember.joinCrew(user, crew);

		crewMemberService.save(crewMember);
		invitationService.delete(invitation);
		return new CrewIdResponse(crew.getId());
	}
}
