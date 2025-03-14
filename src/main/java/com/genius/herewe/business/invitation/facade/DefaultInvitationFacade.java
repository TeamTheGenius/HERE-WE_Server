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
		// 1. user Nickname 존재 여부 확인 -> 없으면 존재하지 않는 사용자, 있으면 이메일 추출
		String nickname = invitationRequest.nickname();

		User user = userService.findByNickname(nickname)
			.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
		// 3. crewId를 통해 Crew에서 필요한 정보(이름, 소개글, 참여인원) 추출
		Crew crew = crewService.findById(invitationRequest.crewId());

		// 2. CrewMember 여부 확인 -> 있으면 이미 크루에 참여해 있는 사용자
		if (crewMemberService.findOptional(user.getId(), crew.getId()).isPresent()) {
			throw new BusinessException(ALREADY_JOINED_CREW);
		}

		// 3. Invitation 여부 확인
		// 3-1. 있으며 expiredAt이 아직 지나지 않은 경우 -> 재발송하고 Invitation 정보 갱신하기 (기존 토큰으로 참여 시도 시 예외 처리 필요)
		//      OR 예외를 발생하고 사용자에게 재발송할건지 확인받기
		// 3-2. 없는 경우 다음 로직 실행
		// 3-3. 있지만 expiredAt이 지난 경우 -> expiredAt만 갱신하면 됨
		// 일단 Invitation 생성하기
		Invitation newInvitation = Invitation.create(2);
		newInvitation.inviteUser(user, crew);

		Invitation invitation = invitationService.findOptional(user.getId(), crew.getId())
			.map(existingInvitation -> {
				existingInvitation.update(newInvitation);
				return existingInvitation;
			})
			.orElseGet(() -> newInvitation);

		// 4. mailManager를 통해 메일 전송
		// 추후 비동기 처리 필요 -> 콜백 여부에 따라 Invitation 추가 처리 필요
		MailRequest mailRequest = MailRequest.builder()
			.receiverMail(user.getEmail())
			.nickname(nickname)
			.crewName(crew.getName())
			.introduce(crew.getIntroduce())
			.memberCount(crew.getParticipantCount())
			.inviteUrl(BASE_URL + INVITE_URL + invitation.getToken())
			.build();
		mailManager.send(mailRequest);

		// 5. (동기) 메일 전송이 완료되면 Invitation 엔티티 저장
		invitationService.save(invitation);
	}

	// 수정 필요
	@Transactional
	public void joinCrew(String inviteToken) {
		// 3. Invitation 여부 확인
		// 3-1. 없거나 expiredAt이 현재 시간을 이미 지난 경우 -> 예외 발생, 유효기간이 지났습니다 + 엔티티 삭제
		// 3-2. 있으며 expiredAt이 현재 시간을 안 지난 경우 -> 다음 로직 실행
		Invitation invitation = invitationService.findByToken(inviteToken);
		if (invitation.getExpiredAt().isBefore(LocalDateTime.now())) {
			invitationService.delete(invitation);
			throw new BusinessException(INVITATION_EXPIRED);
		}
		// N+1 problem 확인해보기
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
