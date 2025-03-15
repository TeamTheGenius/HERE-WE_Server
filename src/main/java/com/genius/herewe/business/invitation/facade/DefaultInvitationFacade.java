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

@Service
@Transactional(readOnly = true)
public class DefaultInvitationFacade implements InvitationFacade {
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
			.orElseGet(() -> Invitation.create(
				invitationInfo.token(), invitationInfo.invitedAt(), invitationInfo.expiredAt()));

		//TODO: 추후 비동기 처리 필요 -> 콜백 여부에 따라 Invitation 추가 처리 필요
		MailRequest mailRequest = MailRequest.builder()
			.receiverMail(user.getEmail())
			.nickname(nickname)
			.crewName(crew.getName())
			.introduce(crew.getIntroduce())
			.memberCount(crew.getParticipantCount())
			.inviteUrl(BASE_URL + INVITE_URL + invitation.getToken())
			.build();
		mailManager.send(mailRequest);

		// NOTE: (동기) 메일 전송이 완료되면 Invitation 엔티티 저장
		invitationService.save(invitation);
	}

	@Transactional
	public void joinCrew(String inviteToken) {
		Invitation invitation = invitationService.findByToken(inviteToken);
		if (invitation.getExpiredAt().isBefore(LocalDateTime.now())) {
			//TODO: 트랜잭션으로 인해 예외만 전달되고 엔티티 삭제가 안됨. 수정하기
			invitationService.delete(invitation);
			throw new BusinessException(INVITATION_EXPIRED);
		}
		//NOTE: N+1 problem 확인해보기
		User user = userService.findById(invitation.getUser().getId());
		Crew crew = crewService.findById(invitation.getCrew().getId());

		Optional<CrewMember> optionalCrewMember = crewMemberService.findOptional(user.getId(), crew.getId());
		if (optionalCrewMember.isPresent()) {
			throw new BusinessException(ALREADY_JOINED_CREW);

		}

		CrewMember crewMember = CrewMember.createByRole(CrewRole.MEMBER);
		crewMember.joinCrew(user, crew);

		crewMemberService.save(crewMember);
		invitationService.delete(invitation);
	}
}
