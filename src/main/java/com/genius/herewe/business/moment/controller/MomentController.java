package com.genius.herewe.business.moment.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

import com.genius.herewe.business.moment.dto.MomentMemberResponse;
import com.genius.herewe.business.moment.dto.MomentPreviewResponse;
import com.genius.herewe.business.moment.dto.MomentRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.business.moment.facade.MomentFacade;
import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.PagingResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.global.response.SlicingResponse;
import com.genius.herewe.core.security.annotation.HereWeUser;
import com.genius.herewe.core.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/moment")
public class MomentController implements MomentApi {
	private final MomentFacade momentFacade;

	@GetMapping("/crew/{crewId}")
	public PagingResponse<MomentPreviewResponse> inquiryMomentList(@HereWeUser User user,
		@PathVariable Long crewId,
		@PageableDefault(size = 20) Pageable pageable) {

		LocalDateTime now = LocalDateTime.now();
		Page<MomentPreviewResponse> momentPreviewResponses = momentFacade.inquiryList(
			user.getId(), crewId, now, pageable);
		return new PagingResponse<>(HttpStatus.OK, momentPreviewResponses);
	}

	@GetMapping("/{momentId}")
	public SingleResponse<MomentResponse> inquirySingleMoment(@HereWeUser User user, @PathVariable Long momentId) {
		MomentResponse momentResponse = momentFacade.inquirySingle(user, momentId);
		return new SingleResponse<>(HttpStatus.OK, momentResponse);
	}

	@PostMapping("/{momentId}/join")
	public SingleResponse<MomentResponse> joinMoment(@HereWeUser User user, @PathVariable Long momentId) {
		LocalDateTime now = LocalDateTime.now();
		MomentResponse momentResponse = momentFacade.join(user.getId(), momentId, now);

		return new SingleResponse<>(HttpStatus.OK, momentResponse);
	}

	@DeleteMapping("/{momentId}/join")
	public CommonResponse quitMoment(@HereWeUser User user, @PathVariable Long momentId) {
		LocalDateTime now = LocalDateTime.now();
		momentFacade.quit(user.getId(), momentId, now);
		return CommonResponse.ok();
	}

	@PostMapping
	public SingleResponse<MomentResponse> createMoment(@HereWeUser User user,
		@RequestParam(name = "crewId") Long crewId,
		@RequestBody MomentRequest momentRequest) {

		MomentResponse momentResponse = momentFacade.create(user.getId(), crewId, momentRequest);
		return new SingleResponse<>(HttpStatus.CREATED, momentResponse);
	}

	@PatchMapping("/{momentId}")
	public SingleResponse<MomentResponse> modifyMoment(@PathVariable Long momentId,
		@RequestBody MomentRequest momentRequest) {

		MomentResponse momentResponse = momentFacade.modify(momentId, momentRequest);
		return new SingleResponse<>(HttpStatus.OK, momentResponse);
	}

	@DeleteMapping("/{momentId}")
	public CommonResponse deleteMoment(@PathVariable Long momentId) {
		momentFacade.delete(momentId);
		return CommonResponse.ok();
	}

	@GetMapping("/{momentId}/members")
	public SlicingResponse<MomentMemberResponse> inquiryJoinedMembers(@PathVariable Long momentId,
		@PageableDefault(size = 20) Pageable pageable) {

		Slice<MomentMemberResponse> momentMemberResponses = momentFacade.inquiryJoinedMembers(momentId, pageable);
		return new SlicingResponse<>(HttpStatus.OK, momentMemberResponses);
	}
}
