package com.genius.herewe.business.moment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genius.herewe.business.moment.dto.MomentCreateRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.business.moment.facade.MomentFacade;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.security.annotation.HereWeUser;
import com.genius.herewe.core.user.domain.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MomentController {
	private final MomentFacade momentFacade;

	@PostMapping("/moment")
	public SingleResponse<MomentResponse> createMoment(@HereWeUser User user,
		@RequestBody MomentCreateRequest momentCreateRequest) {

		MomentResponse momentResponse = momentFacade.createMoment(user.getId(), momentCreateRequest);
		return new SingleResponse<>(HttpStatus.OK, momentResponse);
	}
}
