package com.genius.herewe.business.invitation.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.domain.CrewRole;
import com.genius.herewe.business.crew.fixture.CrewFixture;
import com.genius.herewe.business.crew.service.CrewMemberService;
import com.genius.herewe.business.crew.service.CrewService;
import com.genius.herewe.business.invitation.Fixture.InvitationFixture;
import com.genius.herewe.business.invitation.domain.Invitation;
import com.genius.herewe.business.invitation.dto.InvitationRequest;
import com.genius.herewe.business.invitation.service.InvitationService;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.fixture.UserFixture;
import com.genius.herewe.core.user.service.UserService;
import com.genius.herewe.infra.mail.service.MailManager;

@ExtendWith(MockitoExtension.class)
class InvitationFacadeTest {
	private InvitationFacade invitationFacade;
	@Mock
	private UserService userService;
	@Mock
	private CrewService crewService;
	@Mock
	private CrewMemberService crewMemberService;
	@Mock
	private InvitationService invitationService;
	@Mock
	private MailManager mailManager;
	private User user;
	private Crew crew;
	private InvitationRequest invitationRequest;

	@BeforeEach
	void init() {
		invitationFacade = new DefaultInvitationFacade("http://BASE_URL", "/invite/",
			userService, crewService, crewMemberService, invitationService, mailManager);
		user = UserFixture.createDefault();
		crew = CrewFixture.createDefault();
		invitationRequest = new InvitationRequest(crew.getId(), user.getNickname());
	}

	@Nested
	@DisplayName("특정 사용자에게 크루 초대 요청 시")
	class Context_when_invite_to_crew {

		@Nested
		@DisplayName("초대 대상의 닉네임을 전달했을 때")
		class Describe_pass_nickname {
			@Test
			@DisplayName("존재하지 않는 닉네임이라면 MEMBER_NOT_FOUND 예외가 발생한다.")
			public void it_throws_MEMBER_NOT_FOUND_exception() {
				//given
				String fakeNickname = "fake Nickname";
				given(userService.findByNickname(fakeNickname)).willThrow(new BusinessException(MEMBER_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> invitationFacade.inviteCrew(
					new InvitationRequest(crew.getId(), fakeNickname)
				))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
			}
		}

		@Nested
		@DisplayName("초대할 크루의 식별자(PK)를 전달했을 때")
		class Describe_pass_crew_PK {
			@Test
			@DisplayName("존재하지 않는다면 CREW_NOT_FOUND 예외가 발생한다.")
			public void it_throws_CREW_NOT_FOUND_exception() {
				//given
				Long fakeCrewId = 999L;
				given(userService.findByNickname(user.getNickname())).willReturn(Optional.of(user));
				given(crewService.findById(fakeCrewId)).willThrow(new BusinessException(CREW_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> invitationFacade.inviteCrew(
					new InvitationRequest(fakeCrewId, user.getNickname())
				))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(CREW_NOT_FOUND.getMessage());
			}
		}

		@Nested
		@DisplayName("크루 참여 정보를 조회했을 때")
		class Describe_inquiry_crewMember {
			@Test
			@DisplayName("크루 참여 정보가 존재한다면 ALREADY_JOINED_CREW 예외가 발생한다.")
			public void it_throws_ALREADY_JOINED_CREW_exception() {
				//given
				Long userId = user.getId();
				Long crewId = crew.getId();
				given(userService.findByNickname(user.getNickname())).willReturn(Optional.of(user));
				given(crewService.findById(crew.getId())).willReturn(crew);
				given(crewMemberService.findOptional(userId, crewId)).willReturn(
					Optional.of(CrewMember.createByRole(CrewRole.MEMBER)));

				//when & then
				assertThatThrownBy(() -> invitationFacade.inviteCrew(invitationRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(ALREADY_JOINED_CREW.getMessage());
			}
		}

		@Nested
		@DisplayName("초대 엔티티를 조회했을 때")
		class Describe_inquiry_invitation {
			Long userId;
			Long crewId;
			@Captor
			private ArgumentCaptor<Invitation> invitationCaptor;

			@BeforeEach
			void init() {
				userId = user.getId();
				crewId = crew.getId();
				given(userService.findByNickname(user.getNickname())).willReturn(Optional.of(user));
				given(crewService.findById(crew.getId())).willReturn(crew);
				given(crewMemberService.findOptional(userId, crewId)).willReturn(Optional.empty());
			}

			@Test
			@DisplayName("기존에 존재하지 않았다면 새로운 Invitation 엔티티를 생성한다.")
			public void it_return_new_invitation_entity() {
				//given
				given(invitationService.findOptional(userId, crewId)).willReturn(Optional.empty());

				//when
				invitationFacade.inviteCrew(invitationRequest);

				//then
				verify(invitationService).save(invitationCaptor.capture());
				Invitation savedInvitation = invitationCaptor.getValue();

				assertThat(savedInvitation.getToken()).isNotNull();
				assertThat(savedInvitation.getInvitedAt()).isNotNull();
				assertThat(savedInvitation.getExpiredAt()).isAfter(LocalDateTime.now());
			}

			@Test
			@DisplayName("기존에 존재했으나 expiredAt이 지나지 않은 경우, token, invitedAt, expiredAt이 갱신된다.")
			public void it_update_invitation() {
				//given
				Invitation existInvitation = InvitationFixture.createDefault();
				existInvitation.inviteUser(user, crew);
				String inviteToken = existInvitation.getToken();
				LocalDateTime invitedAt = existInvitation.getInvitedAt();
				LocalDateTime expiredAt = existInvitation.getExpiredAt();

				given(invitationService.findOptional(userId, crewId)).willReturn(Optional.of(existInvitation));

				//when
				invitationFacade.inviteCrew(invitationRequest);

				//then
				verify(invitationService).save(invitationCaptor.capture());
				Invitation updatedInvitation = invitationCaptor.getValue();

				assertThat(updatedInvitation.getToken()).isNotEqualTo(inviteToken);
				assertThat(updatedInvitation.getInvitedAt()).isNotEqualTo(invitedAt);
				assertThat(updatedInvitation.getExpiredAt()).isNotEqualTo(expiredAt);
				assertThat(updatedInvitation.getExpiredAt()).isAfter(LocalDateTime.now());
			}

			@Test
			@DisplayName("기존에 존재했고 expiredAt이 지난 경우, token, invitedAt, expiredAt이 갱신된다.")
			public void it_update_only_expiredAt() {
				//given
				Invitation existInvitation = InvitationFixture.createExpired();
				existInvitation.inviteUser(user, crew);
				String inviteToken = existInvitation.getToken();
				LocalDateTime invitedAt = existInvitation.getInvitedAt();
				LocalDateTime expiredAt = existInvitation.getExpiredAt();

				//when
				invitationFacade.inviteCrew(invitationRequest);

				//then
				verify(invitationService).save(invitationCaptor.capture());
				Invitation updatedInvitation = invitationCaptor.getValue();

				assertThat(updatedInvitation.getToken()).isNotEqualTo(inviteToken);
				assertThat(updatedInvitation.getInvitedAt()).isNotEqualTo(invitedAt);
				assertThat(updatedInvitation.getExpiredAt()).isNotEqualTo(expiredAt);
				assertThat(updatedInvitation.getExpiredAt()).isAfter(LocalDateTime.now());
			}
		}
	}

	@Nested
	@DisplayName("초대 토큰을 통해 크루 참여 요청 시")
	class Context_request_join_crew_by_token {
		Long userId;
		Long crewId;

		@BeforeEach
		void init() {
			userId = user.getId();
			crewId = crew.getId();
		}

		@Nested
		@DisplayName("토큰을 통해 Invitation 엔티티 조회 시")
		class Describe_inquiry_invitation_by_token {
			@Test
			@DisplayName("Invitation 엔티티를 찾을 수 없다면 INVITATION_NOT_FOUND 예외가 발생한다.")
			public void it_throws_INVITATION_NOT_FOUND_exception() {
				//given
				String invalidToken = "invalid invitation token";
				given(invitationService.findByToken(invalidToken)).willThrow(
					new BusinessException(INVITATION_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> invitationFacade.joinCrew(invalidToken))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(INVITATION_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("Invitation의 expiredAt이 만료되었다면 INVITATION_EXPIRED 예외를 발생한다.")
			public void it_throws_INVITATION_EXPIRED_exception() {
				//given
				Invitation expiredInvitation = InvitationFixture.createExpired();
				String expiredToken = expiredInvitation.getToken();

				given(invitationService.findByToken(expiredToken)).willReturn(expiredInvitation);

				//when & then
				assertThatThrownBy(() -> invitationFacade.joinCrew(expiredToken))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(INVITATION_EXPIRED.getMessage());
			}
		}

		@Nested
		@DisplayName("참여 정보를 조회했을 때")
		class Describe_inquiry_crewMember {
			@BeforeEach
			void init() {
				given(userService.findById(userId)).willReturn(user);
				given(crewService.findById(crew.getId())).willReturn(crew);
				given(crewMemberService.findOptional(userId, crewId)).willReturn(Optional.empty());
			}

			@Test
			@DisplayName("참여 정보가 이미 존재한다면 ALREADY_JOINED_CREW 예외가 발생한다.")
			public void it_throws_ALREADY_JOINED_CREW_exception() {
				//given
				Invitation invitation = InvitationFixture.createDefault();
				invitation.inviteUser(user, crew);
				CrewMember crewMember = CrewMember.createByRole(CrewRole.MEMBER);

				given(invitationService.findByToken(invitation.getToken())).willReturn(invitation);
				given(crewMemberService.findOptional(userId, crewId)).willReturn(Optional.of(crewMember));

				//when & then
				assertThatThrownBy(() -> invitationFacade.joinCrew(invitation.getToken()))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(ALREADY_JOINED_CREW.getMessage());
			}

			@Test
			@DisplayName("참여 정보가 존재하지 않는다면 새로운 CrewMember 엔티티를 저장하고, Invitation 엔티티를 삭제한다.")
			public void it_save_crewMember_and_delete_invitation() {
				//given
				Invitation invitation = InvitationFixture.createDefault();
				invitation.inviteUser(user, crew);
				String invitationToken = invitation.getToken();

				given(invitationService.findByToken(invitationToken)).willReturn(invitation);
				given(crewMemberService.findOptional(userId, crewId)).willReturn(Optional.empty());

				ArgumentCaptor<CrewMember> crewMemberCaptor = ArgumentCaptor.forClass(CrewMember.class);

				//when
				invitationFacade.joinCrew(invitationToken);

				//then
				verify(crewMemberService).save(crewMemberCaptor.capture());
				verify(invitationService).delete(invitation);
				CrewMember savedCrewMember = crewMemberCaptor.getValue();
				assertThat(savedCrewMember.getUser().getId()).isEqualTo(userId);
				assertThat(savedCrewMember.getCrew().getId()).isEqualTo(crewId);
				assertThat(savedCrewMember.getJoinedAt()).isNotNull();
			}
		}
	}
}