package com.genius.herewe.business.crew.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewExpelRequest;
import com.genius.herewe.business.crew.dto.CrewIdResponse;
import com.genius.herewe.business.crew.dto.CrewLeaderTransferRequest;
import com.genius.herewe.business.crew.dto.CrewMemberResponse;
import com.genius.herewe.business.crew.dto.CrewModifyRequest;
import com.genius.herewe.business.crew.dto.CrewPreviewResponse;
import com.genius.herewe.business.crew.dto.CrewProfileResponse;
import com.genius.herewe.business.crew.dto.CrewResponse;
import com.genius.herewe.business.crew.facade.CrewFacade;
import com.genius.herewe.business.invitation.dto.InvitationRequest;
import com.genius.herewe.business.invitation.facade.InvitationFacade;
import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.PagingResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.security.annotation.HereWeUser;
import com.genius.herewe.core.user.domain.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/crew")
@RequiredArgsConstructor
public class CrewController implements CrewApi {
	private final CrewFacade crewFacade;
	private final InvitationFacade invitationFacade;

	@GetMapping("/profile/{crewId}")
	public SingleResponse<CrewProfileResponse> inquiryCrewProfile(@HereWeUser User user,
																  @PathVariable Long crewId) {
		CrewProfileResponse crewProfileResponse = crewFacade.inquiryCrewProfile(user.getId(), crewId);
		return new SingleResponse<>(HttpStatus.OK, crewProfileResponse);
	}

	@GetMapping("/my")
	public PagingResponse<CrewPreviewResponse> inquiryMyCrews(
		@HereWeUser User user,
		@PageableDefault(size = 20, page = 0) Pageable pageable) {

		Page<CrewPreviewResponse> crewPreviewResponses = crewFacade.inquiryMyCrews(user.getId(), pageable);
		return new PagingResponse<>(HttpStatus.OK, crewPreviewResponses);
	}

	@GetMapping("/{crewId}")
	public SingleResponse<CrewResponse> inquiryCrewInfo(@HereWeUser User user, @PathVariable Long crewId) {
		CrewResponse crewResponse = crewFacade.inquiryCrew(user.getId(), crewId);

		return new SingleResponse<>(HttpStatus.OK, crewResponse);
	}

	@GetMapping("/{crewId}/members")
	public PagingResponse<CrewMemberResponse> getMemberInfo(
		@PathVariable Long crewId,
		@PageableDefault(size = 10, page = 0) Pageable pageable) {

		Page<CrewMemberResponse> crewMemberResponses = crewFacade.inquiryMembers(crewId, pageable);

		return new PagingResponse<>(HttpStatus.OK, crewMemberResponses);
	}

	@PostMapping
	public SingleResponse<CrewPreviewResponse> createCrew(
		@HereWeUser User user,
		@Valid @RequestBody CrewCreateRequest crewCreateRequest) {

		CrewPreviewResponse response = crewFacade.createCrew(user.getId(), crewCreateRequest);

		return new SingleResponse<>(HttpStatus.CREATED, response);
	}

	@PatchMapping("/{crewId}")
	public SingleResponse<CrewPreviewResponse> modifyCrew(
		@HereWeUser User user,
		@PathVariable Long crewId,
		@RequestBody CrewModifyRequest crewModifyRequest) {

		CrewPreviewResponse response = crewFacade.modifyCrew(user.getId(), crewId, crewModifyRequest);

		return new SingleResponse<>(HttpStatus.OK, response);
	}

	@DeleteMapping("/{crewId}")
	public CommonResponse deleteCrew(@PathVariable Long crewId) {
		crewFacade.deleteCrew(crewId);
		return CommonResponse.ok();
	}

	@PostMapping("/invite")
	public CommonResponse inviteCrew(
		@Valid @RequestBody InvitationRequest invitationRequest) {

		invitationFacade.inviteCrew(invitationRequest);

		return CommonResponse.ok();
	}

	@PostMapping("/invite/{token}")
	public SingleResponse<CrewIdResponse> joinCrew(@PathVariable(name = "token") String inviteToken) {
		CrewIdResponse crewIdResponse = invitationFacade.joinCrew(inviteToken);

		return new SingleResponse<>(HttpStatus.OK, crewIdResponse);
	}

	@DeleteMapping("/{crewId}/members")
	public CommonResponse expelCrew(
		@HereWeUser User user, @PathVariable Long crewId,
		@RequestParam(name = "nickname") String nickname) {

		crewFacade.expelCrew(user.getId(), new CrewExpelRequest(crewId, nickname));

		return CommonResponse.ok();
	}

	@DeleteMapping("/{crewId}/members/me")
	public CommonResponse quitCrew(@HereWeUser User user,
								   @PathVariable Long crewId) {
		crewFacade.quitCrew(user.getId(), crewId);
		return CommonResponse.ok();
	}

	@PatchMapping("/{crewId}/members/leader")
	public CommonResponse handOverLeader(@HereWeUser User user, @PathVariable Long crewId,
										 @RequestBody @Valid CrewLeaderTransferRequest transferRequest) {

		crewFacade.handoverLeader(crewId, user.getId(), transferRequest.nickname());
		return CommonResponse.ok();
	}
}
