package com.genius.herewe.business.crew.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewModifyRequest;
import com.genius.herewe.business.crew.dto.CrewResponse;
import com.genius.herewe.business.crew.facade.CrewFacade;
import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.security.annotation.HereWeUser;
import com.genius.herewe.core.user.domain.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/crew")
@RequiredArgsConstructor
public class CrewController {
	private final CrewFacade crewFacade;

	@PostMapping
	public SingleResponse<CrewResponse> createCrew(
		@HereWeUser User user,
		@RequestBody CrewCreateRequest crewCreateRequest) {

		CrewResponse response = crewFacade.createCrew(user.getId(), crewCreateRequest);

		return new SingleResponse<>(HttpStatus.CREATED, response);
	}

	@PatchMapping("/{crewId}")
	public SingleResponse<CrewResponse> modifyCrew(
		@HereWeUser User user,
		@PathVariable Long crewId,
		@RequestBody CrewModifyRequest crewModifyRequest) {

		CrewResponse response = crewFacade.modifyCrew(user.getId(), crewId, crewModifyRequest);

		return new SingleResponse<>(HttpStatus.CREATED, response);
	}

	@PostMapping("/{crewId}/members")
	public CommonResponse joinCrew(@HereWeUser User user, @PathVariable Long crewId) {
		crewFacade.joinCrew(user.getId(), crewId);

		return CommonResponse.ok();
	}
}
