package com.genius.herewe.business.moment.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.genius.herewe.business.crew.fixture.CrewFixture;
import com.genius.herewe.business.crew.service.CrewService;
import com.genius.herewe.business.location.domain.Location;
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.business.location.service.LocationService;
import com.genius.herewe.business.moment.domain.Moment;
import com.genius.herewe.business.moment.dto.MomentRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.business.moment.fixture.MomentFixture;
import com.genius.herewe.business.moment.service.MomentService;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.user.fixture.UserFixture;
import com.genius.herewe.core.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class MomentFacadeTest {
	Place place = Place.builder()
		.name("하이디라오 강남점")
		.x(127.02)
		.y(37.50)
		.address("지번 주소")
		.roadAddress("도로명 주소")
		.url("kakao map url")
		.phone("전화 번호")
		.build();
	@InjectMocks
	private DefaultMomentFacade momentFacade;
	@Mock
	private UserService userService;
	@Mock
	private CrewService crewService;
	@Mock
	private MomentService momentService;
	@Mock
	private LocationService locationService;

	@Nested
	@DisplayName("모먼트 생성 시")
	class Context_create_moment {
		@Nested
		@DisplayName("모먼트 생성 DTO의 유효성 검사 시")
		class Describe_pass_moment_create_info {
			static Stream<Arguments> provideInvalidRequests() {
				Place place = Place.builder().build();
				return Stream.of(
					Arguments.of(new MomentRequest("Test", null, place, 5, LocalDateTime.now()), "meetAt"),
					Arguments.of(new MomentRequest("Test", LocalDateTime.now(), place, null, LocalDateTime.now()),
						"capacity"),
					Arguments.of(new MomentRequest("Test", LocalDateTime.now(), place, 5, null), "closedAt")
				);
			}

			@ParameterizedTest
			@DisplayName("capacity, meetAt, closedAt 중 하나라도 NULL인 경우 REQUIRED_FIELD_MISSING 예외가 발생한다.")
			@MethodSource("provideInvalidRequests")
			public void it_throws_REQUIRED_FIELD_MISSING_exception(MomentRequest request, String fieldName) {
				assertThatThrownBy(() -> momentFacade.createMoment(1L, 1L, request))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(REQUIRED_FIELD_MISSING.getMessage());
			}

			@ParameterizedTest
			@DisplayName("마감 인원이 2 미만이라면 INVALID_MOMENT_CAPACITY 예외를 발생한다.")
			@ValueSource(ints = {-1, 0, 1})
			public void it_throws_INVALID_MOMENT_CAPACITY_exception(int capacity) {
				//given
				MomentRequest momentRequest = MomentRequest.builder()
					.capacity(capacity)
					.meetAt(LocalDateTime.now().plusDays(10))
					.closedAt(LocalDateTime.now().plusDays(2))
					.build();

				//when & then
				assertThatThrownBy(() -> momentFacade.createMoment(1L, 1L, momentRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(INVALID_MOMENT_CAPACITY.getMessage());
			}

			@Test
			@DisplayName("meetAt, closedAt 중 하나라도 현재 시간보다 이전이라면 INVALID_MOMENT_DATE 예외를 발생한다.")
			public void it_throws_INVALID_MOMENT_DATE_exception() {
				//given
				MomentRequest momentRequest1 = MomentRequest.builder()
					.capacity(10)
					.meetAt(LocalDateTime.now().minusDays(1))
					.closedAt(LocalDateTime.now().plusDays(2))
					.build();
				MomentRequest momentRequest2 = MomentRequest.builder()
					.capacity(10)
					.meetAt(LocalDateTime.now().plusDays(1))
					.closedAt(LocalDateTime.now().minusDays(2))
					.build();

				//when & then
				assertThatThrownBy(() -> momentFacade.createMoment(1L, 2L, momentRequest1))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(INVALID_MOMENT_DATE.getMessage());

				assertThatThrownBy(() -> momentFacade.createMoment(1L, 2L, momentRequest2))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(INVALID_MOMENT_DATE.getMessage());
			}
		}

		@Nested
		@DisplayName("모먼트 생성에 필요한 DTO 전달 시")
		class Describe_pass_moment_info {
			Long userId = 1L;
			Long crewId = 1L;
			MomentRequest momentRequest;

			@BeforeEach
			void init() {
				given(userService.findById(userId)).willReturn(UserFixture.createDefault());
				given(crewService.findById(crewId)).willReturn(CrewFixture.createDefault());
				momentRequest = MomentRequest.builder()
					.momentName("모먼트 이름")
					.capacity(10)
					.meetAt(LocalDateTime.now().plusDays(10))
					.closedAt(LocalDateTime.now().plusDays(2))
					.place(place)
					.build();
			}

			@Test
			@DisplayName("Moment 엔티티에 DTO의 내용이 들어간다.")
			public void it_contains_dto_contents() {
				//given
				given(locationService.saveFromPlace(place, 1)).willReturn(Location.createFromPlace(place, 1));

				//when
				momentFacade.createMoment(userId, crewId, momentRequest);

				//then
				ArgumentCaptor<Moment> momentCaptor = ArgumentCaptor.forClass(Moment.class);
				verify(momentService).save(momentCaptor.capture());
				Moment captoredMoment = momentCaptor.getValue();
				assertThat(captoredMoment.getName()).isEqualTo(momentRequest.momentName());
				assertThat(captoredMoment.getMeetAt()).isEqualTo(momentRequest.meetAt());
				assertThat(captoredMoment.getClosedAt()).isEqualTo(momentRequest.closedAt());
				assertThat(captoredMoment.getCapacity()).isEqualTo(momentRequest.capacity());
				assertThat(captoredMoment.getParticipantCount()).isEqualTo(1);
			}

			@Test
			@DisplayName("Location 엔티티에 Place의 정보가 담긴다.")
			public void it_contains_place_in_location() {
				//given
				Location expectedLocation = Location.createFromPlace(place, 1);
				given(locationService.saveFromPlace(any(Place.class), anyInt())).willReturn(expectedLocation);

				//when
				momentFacade.createMoment(userId, crewId, momentRequest);

				//then
				ArgumentCaptor<Place> placeCaptor = ArgumentCaptor.forClass(Place.class);
				ArgumentCaptor<Integer> orderCaptor = ArgumentCaptor.forClass(Integer.class);

				verify(locationService).saveFromPlace(placeCaptor.capture(), orderCaptor.capture());

				Place capturedPlace = placeCaptor.getValue();
				Integer capturedOrder = orderCaptor.getValue();

				assertThat(capturedPlace).isEqualTo(place);
				assertThat(capturedOrder).isEqualTo(1);

				ArgumentCaptor<Moment> momentCaptor = ArgumentCaptor.forClass(Moment.class);
				verify(momentService).save(momentCaptor.capture());
			}
		}
	}

	@Nested
	@DisplayName("모먼트 수정 시")
	class Context_modify_moment {
		LocalDateTime now = LocalDateTime.now();
		MomentRequest momentRequest = MomentRequest.builder()
			.momentName("새로운 모먼트 이름")
			.capacity(10)
			.meetAt(now.plusDays(20))
			.closedAt(now.plusDays(5))
			.place(place)
			.build();

		@Nested
		@DisplayName("모먼트 수정에 필요한 정보 전달 시")
		class Describe_pass_info {
			@Test
			@DisplayName("momentId를 통해 모먼트 엔티티를 찾을 수 없는 경우 MOMENT_NOT_FOUND 예외가 발생한다.")
			public void it_throws_MOMENT_NOT_FOUND_exception() {
				//given
				Long fakeMomentId = 999L;
				given(momentService.findById(fakeMomentId)).willThrow(new BusinessException(MOMENT_NOT_FOUND));

				//when & then
				assertThatThrownBy(() -> momentFacade.modifyMoment(fakeMomentId, momentRequest))
					.isInstanceOf(BusinessException.class)
					.hasMessageContaining(MOMENT_NOT_FOUND.getMessage());
			}

			@Test
			@DisplayName("모든 필드가 비어있지 않고 유효할 때 모든 필드가 정상적으로 변경된다.")
			public void it_updates_all_field() {
				//given
				Moment moment = MomentFixture.createDefault();
				Location location = Location.createFromPlace(place, 1);
				given(momentService.findById(anyLong())).willReturn(moment);
				given(locationService.findMeetLocation(anyLong())).willReturn(location);

				//when
				MomentResponse momentResponse = momentFacade.modifyMoment(1L, momentRequest);

				//then
				assertThat(momentResponse).isNotNull();
				assertThat(momentResponse.isJoined()).isTrue();
				assertThat(momentResponse.name()).isEqualTo(momentRequest.momentName());
				assertThat(momentResponse.capacity()).isEqualTo(momentRequest.capacity());
				assertThat(momentResponse.closedAt()).isEqualTo(momentRequest.closedAt());
			}
		}

		@Nested
		@DisplayName("일부 필드만 변경 시")
		class Describe_change_partial_fields {
			Long momentId = 1L;
			Moment moment = MomentFixture.createDefault();

			@BeforeEach
			void init() {
				given(momentService.findById(momentId)).willReturn(moment);
			}

			@Test
			@DisplayName("이름만 변경하면 이름만 업데이트된다.")
			public void it_updates_only_name() {
				//given
				MomentRequest request = MomentRequest.builder()
					.momentName("새로운 모먼트 이름")
					.build();

				//when
				MomentResponse momentResponse = momentFacade.modifyMoment(momentId, request);

				//then
				assertThat(momentResponse.name()).isEqualTo(request.momentName());
			}

			@Test
			@DisplayName("마감 인원만 변경하면 마감 인원만 업데이트된다.")
			public void it_updates_only_capacity() {
				//given
				MomentRequest request = MomentRequest.builder()
					.capacity(moment.getCapacity() + 1)
					.build();

				//when
				MomentResponse momentResponse = momentFacade.modifyMoment(momentId, request);

				//then
				assertThat(momentResponse.capacity()).isEqualTo(request.capacity());
			}

			@Test
			@DisplayName("만남 일자만 변경하면 만남 일자만 업데이트된다.")
			public void it_updates_only_meetAt() {
				//given
				String originalName = moment.getName();
				int originalCapacity = moment.getCapacity();
				LocalDateTime originalClosedAt = moment.getClosedAt();
				MomentRequest request = MomentRequest.builder()
					.meetAt(moment.getMeetAt().plusDays(1))
					.build();

				//when
				momentFacade.modifyMoment(momentId, request);

				//then
				assertThat(moment.getMeetAt()).isEqualTo(request.meetAt());

				assertThat(moment.getName()).isEqualTo(originalName);
				assertThat(moment.getCapacity()).isEqualTo(originalCapacity);
				assertThat(moment.getClosedAt()).isEqualTo(originalClosedAt);
			}

			@Test
			@DisplayName("마감 일자만 변경하면 마감 일자만 업데이트된다.")
			public void it_updates_only_closedAt() {
				//given
				MomentRequest request = MomentRequest.builder()
					.closedAt(moment.getClosedAt().plusDays(1))
					.build();

				//when
				MomentResponse momentResponse = momentFacade.modifyMoment(momentId, request);

				//then
				assertThat(momentResponse.closedAt()).isEqualTo(request.closedAt());
			}

			@Test
			@DisplayName("장소만 변경하면 장소만 업데이트된다.")
			public void it_updates_only_location() {
				//given
				Place newPlace = Place.builder()
					.name("새로운 장소")
					.address("new address")
					.roadAddress("new road address")
					.url("new url")
					.x(120.23)
					.y(134.23)
					.phone("new phone")
					.build();
				MomentRequest request = MomentRequest.builder().place(newPlace).build();

				String originalName = moment.getName();
				int originalCapacity = moment.getCapacity();
				LocalDateTime originalClosedAt = moment.getClosedAt();
				LocalDateTime originalMeetAt = moment.getMeetAt();
				Location originalLocation = mock(Location.class);
				given(locationService.findMeetLocation(momentId)).willReturn(originalLocation);

				//when
				momentFacade.modifyMoment(momentId, request);

				//then
				verify(locationService).findMeetLocation(momentId);

				ArgumentCaptor<Place> placeCaptor = ArgumentCaptor.forClass(Place.class);
				verify(originalLocation).update(placeCaptor.capture());
				Place capturedPlace = placeCaptor.getValue();
				assertThat(capturedPlace).isEqualTo(newPlace);

				assertThat(moment.getName()).isEqualTo(originalName);
				assertThat(moment.getCapacity()).isEqualTo(originalCapacity);
				assertThat(moment.getMeetAt()).isEqualTo(originalMeetAt);
				assertThat(moment.getClosedAt()).isEqualTo(originalClosedAt);
			}
		}
	}
}