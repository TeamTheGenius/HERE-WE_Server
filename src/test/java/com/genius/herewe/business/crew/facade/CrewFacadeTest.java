package com.genius.herewe.business.crew.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.domain.CrewRole;
import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewExpelRequest;
import com.genius.herewe.business.crew.dto.CrewModifyRequest;
import com.genius.herewe.business.crew.dto.CrewPreviewResponse;
import com.genius.herewe.business.crew.dto.CrewResponse;
import com.genius.herewe.business.crew.fixture.CrewFixture;
import com.genius.herewe.business.crew.service.CrewMemberService;
import com.genius.herewe.business.crew.service.CrewService;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.fixture.UserFixture;
import com.genius.herewe.core.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class CrewFacadeTest {
	private CrewFacade crewFacade;
	@Mock
	private UserService userService;
	@Mock
	private CrewService crewService;
	@Mock
	private CrewMemberService crewMemberService;

	@BeforeEach
	void init() {
		crewFacade = new DefaultCrewFacade(userService, crewService, crewMemberService);
	}

	@Nested
	@DisplayName("Crew 생성 시")
	class Context_create_crew {
		CrewCreateRequest crewCreateRequest = new CrewCreateRequest("name", "introduce");

		@Nested
		@DisplayName("생성하려는 사용자의 PK(userId)를 전달했을 때")
		class Describe_pass_userId {
			@Test
			@DisplayName("PK에 해당하는 사용자가 없으면 MEMBER_NOT_FOUND 예외가 발생한다")
			public void it_throws_MEMBER_NOT_FOUND_exception() {
				//given
				Long userId = 999L;
				given(userService.findById(userId)).willThrow(new BusinessException(MEMBER_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> crewFacade.createCrew(userId, crewCreateRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
			}
		}

		@Nested
		@DisplayName("크루 생성에 필요한 DTO를 전달했을 때")
		class Describe_pass_CrewCreateRequest {
			@Test
			@DisplayName("크루 이름과 소개를 전달하면 크루 생성이 정상적으로 완료된다.")
			public void it_return_crew() {
				//given
				User user = UserFixture.createDefault();
				Crew crew = CrewFixture.createByName(crewCreateRequest.name());
				CrewMember crewMember = CrewMember.createByRole(CrewRole.LEADER);
				crewMember.joinCrew(user, crew);

				given(userService.findById(user.getId())).willReturn(user);
				given(crewService.save(any(Crew.class))).willReturn(crew);
				given(crewMemberService.save(any(CrewMember.class))).willReturn(crewMember);

				//when
				CrewPreviewResponse crewPreviewResponse = crewFacade.createCrew(user.getId(), crewCreateRequest);

				//then
				assertThat(crewPreviewResponse).isNotNull();
				assertThat(crewPreviewResponse.name()).isEqualTo(crewCreateRequest.name());
				assertThat(crewPreviewResponse.participantCount()).isEqualTo(1);
			}
		}
	}

	@Nested
	@DisplayName("Crew 수정 시")
	class Context_modify_crew {
		CrewModifyRequest modifyRequest = new CrewModifyRequest("new name", "new introduce");

		@Nested
		@DisplayName("crew 조회를 위한 데이터 전달 시")
		class Describe_pass_crew_info {
			@Test
			@DisplayName("crewId를 전달했을 때 크루를 찾을 수 없다면 CREW_NOT_FOUND 예외를 발생한다.")
			public void it_throws_CREW_NOT_FOUND_exception() {
				//given
				Long userId = 1L;
				Long crewId = 999L;
				given(crewService.findById(crewId)).willThrow(new BusinessException(CREW_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> crewFacade.modifyCrew(userId, crewId, modifyRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(CREW_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("crew 참여 정보를 조회하지 못한다면 CREW_JOIN_INFO_NOT_FOUND 예외를 발생한다.")
			public void it_throws_CREW_JOIN_INFO_NOT_FOUND_exception() {
				//given
				Long userId = 1L;
				Long crewId = 1L;
				Crew crew = CrewFixture.createDefault();
				given(crewService.findById(crewId)).willReturn(crew);
				given(crewMemberService.find(userId, crewId)).willThrow(
					new BusinessException(CREW_JOIN_INFO_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> crewFacade.modifyCrew(userId, crewId, modifyRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(CREW_JOIN_INFO_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("크루 리더가 아니라면 LEADER_PERMISSION_DENIED 예외가 발생한다.")
			public void it_throws_LEADER_PERMISSION_DENIED_exception() {
				//given
				Long userId = 1L;
				Long crewId = 1L;
				Crew crew = CrewFixture.createDefault();
				CrewMember crewMember = CrewMember.createByRole(CrewRole.MEMBER);

				given(crewService.findById(crewId)).willReturn(crew);
				given(crewMemberService.find(userId, crewId)).willReturn(crewMember);

				//when&then
				assertThatThrownBy(() -> crewFacade.modifyCrew(userId, crewId, modifyRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(LEADER_PERMISSION_DENIED.getMessage());
			}
		}

		@Nested
		@DisplayName("크루 수정에 필요한 정보 전달 시")
		class Describe_pass_modify_info {
			@Test
			@DisplayName("이름과 소개글을 수정할 수 있다.")
			public void it_modify_crew() {
				//given
				Long userId = 1L;
				Long crewId = 1L;
				Crew crew = CrewFixture.createDefault();
				CrewMember crewMember = CrewMember.createByRole(CrewRole.LEADER);

				given(crewService.findById(crewId)).willReturn(crew);
				given(crewMemberService.find(userId, crewId)).willReturn(crewMember);

				//when
				CrewPreviewResponse modified = crewFacade.modifyCrew(userId, crewId, modifyRequest);

				//then
				assertThat(modified).isNotNull();
				assertThat(modified.crewId()).isEqualTo(crewId);
				assertThat(modified.name()).isEqualTo(modifyRequest.name());
				assertThat(modified.participantCount()).isEqualTo(crew.getParticipantCount());
			}
		}
	}

	@Nested
	@DisplayName("크루의 정보 조회 시")
	class Context_inquiry_crew_info {
		@Nested
		@DisplayName("crew 조회를 위한 데이터 전달 시")
		class Describe_pass_crew_info {
			@Test
			@DisplayName("crewId를 전달했을 때 크루를 찾을 수 없다면 CREW_NOT_FOUND 예외를 발생한다.")
			public void it_throws_CREW_NOT_FOUND_exception() {
				//given
				Long userId = 1L;
				Long crewId = 999L;
				given(crewService.findById(crewId)).willThrow(new BusinessException(CREW_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> crewFacade.inquiryCrew(userId, crewId))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(CREW_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("crew 참여 정보를 조회하지 못한다면 CREW_JOIN_INFO_NOT_FOUND 예외를 발생한다.")
			public void it_throws_CREW_JOIN_INFO_NOT_FOUND_exception() {
				//given
				Long userId = 1L;
				Long crewId = 1L;
				Crew crew = CrewFixture.createDefault();
				given(crewService.findById(crewId)).willReturn(crew);
				given(crewMemberService.find(userId, crewId)).willThrow(
					new BusinessException(CREW_JOIN_INFO_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> crewFacade.inquiryCrew(userId, crewId))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(CREW_JOIN_INFO_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("crew 데이터 조회에 성공한다면, 크루의 정보를 전달한다.")
			public void it_throws_crew_info() {
				//given
				Long userId = 1L;
				Long crewId = 1L;
				Crew crew = CrewFixture.createDefault();
				CrewMember crewMember = CrewMember.createByRole(CrewRole.MEMBER);
				given(crewService.findById(crewId)).willReturn(crew);
				given(crewMemberService.find(userId, crewId)).willReturn(crewMember);

				//when
				CrewResponse crewResponse = crewFacade.inquiryCrew(userId, crewId);

				//then
				assertThat(crewResponse).isNotNull();
				assertThat(crewResponse.crewId()).isEqualTo(crewId);
				assertThat(crewResponse.name()).isEqualTo(crew.getName());
				assertThat(crewResponse.introduce()).isEqualTo(crew.getIntroduce());
				assertThat(crewResponse.leaderName()).isEqualTo(crew.getLeaderName());
				assertThat(crewResponse.role()).isEqualTo(crewMember.getRole());
				assertThat(crewResponse.participantCount()).isEqualTo(crew.getParticipantCount());
			}
		}
	}

	@Nested
	@DisplayName("특정 사용자를 크루에서 내보낼 때")
	class Context_expel_from_crew {
		User user = UserFixture.createDefault();
		User targetUser = UserFixture.builder().id(2L).nickname("expel target").build();
		Crew crew = CrewFixture.createDefault();
		Long userId = user.getId();
		Long crewId = crew.getId();
		CrewExpelRequest expelRequest = new CrewExpelRequest(crewId, targetUser.getNickname());

		@Nested
		@DisplayName("요청한 사용자의 정보를 전달받았을 때")
		class Describe_pass_request_user_info {
			@Test
			@DisplayName("사용자 식별자를 통해 조회했을 때 사용자가 존재하지 않는다면 MEMBER_NOT_FOUND 예외가 발생한다.")
			public void it_throws_MEMBER_NOT_FOUND_exception() {
				//given
				Long fakeUserId = 999L;
				given(userService.findById(argThat(id -> !id.equals(userId))))
					.willThrow(new BusinessException(MEMBER_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> crewFacade.expelCrew(fakeUserId, expelRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("크루 식별자를 통해 크루를 조회할 때, 존재하지 않으면 CREW_NOT_FOUND 예외가 발생한다.")
			public void it_throws_CREW_NOT_FOUND_exception() {
				//given
				Long fakeCrewId = 999L;
				given(crewService.findById(argThat(id -> !id.equals(expelRequest.crewId()))))
					.willThrow(new BusinessException(CREW_NOT_FOUND));

				//when & then
				assertThatThrownBy(
					() -> crewFacade.expelCrew(userId, new CrewExpelRequest(fakeCrewId, user.getNickname())))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(CREW_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("사용자가 크루에 대해 크루 가입 정보가 없을 때 CREW_JOIN_INFO_NOT_FOUND 예외가 발생한다.")
			public void it_throws_CREW_JOIN_INFO_NOT_FOUND_exception() {
				//given
				given(userService.findById(userId)).willReturn(user);
				given(crewService.findById(crewId)).willReturn(crew);
				given(crewMemberService.find(userId, crewId))
					.willReturn(CrewMember.createByRole(CrewRole.LEADER));
				given(crewMemberService.find(userId, crewId))
					.willThrow(new BusinessException(CREW_JOIN_INFO_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> crewFacade.expelCrew(userId, expelRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(CREW_JOIN_INFO_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("사용자의 ROLE을 확인했을 때 LEADER가 아니라면 LEADER_PERMISSION_DENIED 예외가 발생한다.")
			public void it_throws_LEADER_PERMISSION_DENIED_exception() {
				//given
				given(userService.findById(userId)).willReturn(user);
				given(crewService.findById(crewId)).willReturn(crew);
				given(crewMemberService.find(userId, crewId))
					.willReturn(CrewMember.createByRole(CrewRole.MEMBER));

				//when & then
				assertThatThrownBy(() -> crewFacade.expelCrew(userId, expelRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(LEADER_PERMISSION_DENIED.getMessage());
			}
		}

		@Nested
		@DisplayName("크루에서 내보낼 사용자의 정보를 받아서 조회했을 때")
		class Describe_pass_target_nickname {
			@BeforeEach
			void init() {
				given(userService.findById(userId)).willReturn(user);
				given(crewService.findById(crewId)).willReturn(crew);
			}

			@Test
			@DisplayName("닉네임을 통해 사용자를 조회할 수 없을 때 MEMBER_NOT_FOUND 예외가 발생한다.")
			public void it_throws_MEMBER_NOT_FOUND_exception() {
				//given
				String fakeNickname = "fake nickname";
				given(crewMemberService.find(userId, crewId)).willReturn(CrewMember.createByRole(CrewRole.LEADER));
				given(userService.findByNickname(argThat(nickname -> !nickname.equals(user.getNickname()))))
					.willThrow(new BusinessException(MEMBER_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> crewFacade.expelCrew(userId, new CrewExpelRequest(crewId, fakeNickname)))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("사용자가 해당 크루의 멤버가 아닌 경우 CREW_JOIN_INFO_NOT_FOUND 예외가 발생한다.")
			public void it_throws_CREW_JOIN_INFO_NOT_FOUND_exception() {
				//given
				given(crewMemberService.find(userId, crewId))
					.willReturn(CrewMember.createByRole(CrewRole.LEADER));
				given(userService.findByNickname(targetUser.getNickname())).willReturn(Optional.of(targetUser));
				given(crewMemberService.find(targetUser.getId(), crewId)).willThrow(
					new BusinessException(CREW_JOIN_INFO_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> crewFacade.expelCrew(userId, expelRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(CREW_JOIN_INFO_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("사용자가 해당 크루의 리더인 경우 LEADER_CANNOT_EXPEL 예외가 발생한다.")
			public void it_throws_LEADER_CANNOT_EXPEL_exception() {
				//given
				given(userService.findByNickname(targetUser.getNickname())).willReturn(Optional.of(targetUser));
				given(crewMemberService.find(userId, crewId))
					.willReturn(CrewMember.createByRole(CrewRole.LEADER));
				given(crewMemberService.find(targetUser.getId(), crewId))
					.willReturn(CrewMember.createByRole(CrewRole.LEADER));

				//when & then
				assertThatThrownBy(() -> crewFacade.expelCrew(userId, expelRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(LEADER_CANNOT_EXPEL.getMessage());
			}
		}
	}
}